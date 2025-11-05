package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttProperties;
import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步批量推送服务 - 装饰器模式
 * 包装 AsyncPushService，添加批量缓存和定时发送功能
 * <p>
 * 功能特性：
 * 1. 按采集仪编号(cjyNo)分组缓存数据
 * 2. 支持两种触发条件：数据条数达到阈值或时间间隔达到阈值
 * 3. 自动定时发送缓存数据
 * 4. 提供强制发送接口
 *
 * @author zs
 * @since 2025-09-30
 */
@Slf4j
@RequiredArgsConstructor
public class AsyncBufferPushService {

    private final AsyncPushService asyncPushService;
    private final ForwardMqttProperties config;
    private final Vertx vertx;
    /**
     * 按采集仪编号分组的数据缓存
     * key: deviceId (采集仪编号)
     * value: 数据列表
     */
    private final Map<String, DeviceBuffer> deviceDataBuffer = new ConcurrentHashMap<>();

    /**
     * 每个设备的最后发送时间
     */
    private final Map<String, Long> lastSendTimes = new ConcurrentHashMap<>();

    private final Long MAX_BUFFER_SIZE = 100L;  // 最大缓存条数
    private Long timerId;

    /**
     * 内部类，用于封装每个设备的缓存数据。
     * 无需使用 synchronizedList，因为 compute 操作已保证了对单个 key 的原子性。
     */
    private static class DeviceBuffer {
        final String deviceType;
        final List<List<String>> values = new ArrayList<>(); // 使用普通的 ArrayList

        DeviceBuffer(String deviceType) {
            this.deviceType = deviceType;
        }
    }

    @PostConstruct
    public void init() {
        if (!config.isEnable()) {
            log.info("AsyncBufferPushService 未启用。");
            return;
        }
        // 启动定时发送任务
        startScheduledSendTask();
        log.info("AsyncBufferPushService 初始化完成，最大缓存: {} 条，发送间隔: {}ms",
                MAX_BUFFER_SIZE, config.getSendIntervalMs());
    }

    /**
     * 添加数据列表到缓存
     *
     * @param mqttData 待缓存的数据
     */
    public void push(MqttData mqttData) {
        if (mqttData == null || mqttData.getValue().isEmpty() || mqttData.getDeviceId() == null) {
            return;
        }

        // 如果buffer中某个设备数据过多则直接推送
        deviceDataBuffer.compute(mqttData.getDeviceId(), (key, existingBuffer) -> {
            if (existingBuffer == null) {
                return mqttData;
            } else {
                // 合并两个mqttData数据
                existingBuffer.getValue().addAll(mqttData.getValue());
            }
            if (existingBuffer.getValue().size() >= MAX_BUFFER_SIZE) {
                // 达到阈值，触发发送
                sendBufferedData(mqttData.getDeviceId());
            }
            return existingBuffer;
        });
    }

    /**
     * 启动定时发送任务
     */
    private void startScheduledSendTask() {
        long sendInterval = config.getSendIntervalMs();

        timerId = vertx.setPeriodic(sendInterval, id -> {
            // 确保定时器回调中的代码是非阻塞的
            long currentTime = System.currentTimeMillis();

            // 遍历 keySet 是线程安全的
            deviceDataBuffer.keySet().forEach(deviceId -> {
                Long lastSendTime = lastSendTimes.getOrDefault(deviceId, 0L);
                if (currentTime - lastSendTime < sendInterval) {
                    return;
                }
                sendBufferedData(deviceId);
            });
        });

        log.info("定时发送任务已启动，检查间隔: {} ms", config.getSendIntervalMs());
    }


    /**
     * 强制发送指定设备的所有缓存数据
     *
     * @param deviceId 设备编号
     */
    public void flush(String deviceId) {
        if (deviceId == null) {
            return;
        }
        sendBufferedData(deviceId);
    }

    /**
     * 强制发送所有采集仪的缓存数据
     */
    public void flushAll() {
        if (!config.isEnable()) {
            return;
        }
        deviceDataBuffer.keySet().forEach(this::sendBufferedData);
    }

    /**
     * 发送指定设备的缓存数据
     */
    private void sendBufferedData(String deviceId) {
        MqttData mqttData = deviceDataBuffer.get(deviceId);
        if (mqttData == null) {
            return;
        }

        synchronized (mqttData) {
            deviceDataBuffer.remove(deviceId);
        }

        try {
            // 更新当前时间为发送时间
            mqttData.setSampleTime(System.currentTimeMillis());
            final MqttPubProtocol zd001 = new MqttPubProtocol(config.getDataTag(), Collections.singletonList(mqttData));

            // 调用被装饰的 AsyncPushService
            asyncPushService.push(zd001);

            // 更新最后发送时间
            lastSendTimes.computeIfAbsent(deviceId, k -> System.currentTimeMillis());
            log.debug("发送缓存数据成功，设备: {}，数据条数: {}", deviceId, mqttData.getValue().size());
        } catch (Exception e) {
            log.error("发送缓存数据失败，设备: {}，数据条数: {}", deviceId, mqttData.getValue().size(), e);
        }
    }

    /**
     * 清理资源
     */
    @PreDestroy
    public void shutdown() {
        if (timerId != null) {
            // 取消 Vert.x 定时器
            vertx.cancelTimer(timerId);
            log.info("AsyncBufferPushService 已关闭，Vert.x 定时器已取消。");
        }

        // 发送所有剩余数据
        if (config.isEnable()) {
            flushAll();
        }
    }
}
