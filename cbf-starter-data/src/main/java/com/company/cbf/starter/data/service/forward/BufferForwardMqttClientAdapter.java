package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttAutoStartProperties;
import com.company.cbf.starter.data.entity.DeviceInfo;
import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import com.company.cbf.starter.data.entity.MqttServerGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.DependsOn;

/**
 * 缓冲转发 MQTT 客户端适配器
 * 管理多个 MqttClient 对应的缓存适配器实例，支持自动启动和手动加入设备
 * <p>
 * 功能特性：
 * 1. 支持自动启动设备（通过配置文件）的 useBuffer
 * 2. 支持手动加入设备（调用 ForwardMqttClientService.startWithConnect）
 * 3. 全局设备信息缓存，根据设备 ID 获取设备信息
 * 4. 按设备ID分组缓存数据，直接存储 List<List<String>> 数据
 * 5. 支持两种触发条件：数据条数达到阈值或时间间隔达到阈值
 * 6. 自动定时发送缓存数据
 * 7. 提供强制发送接口
 *
 * @author hzs
 * @since 2025-11-15
 */
@Slf4j
@RequiredArgsConstructor
public class BufferForwardMqttClientAdapter {
    private final Vertx vertx;
    private final ForwardMqttClientService forwardMqttClientService;
    private final ForwardMqttAutoStartProperties config;
    private final ObjectMapper objectMapper;
    private final DeviceInfoManager deviceInfoManager;

    /**
     * 存储每个 MqttClient 对应的缓存适配器实例
     * Key: MqttServerGateway ID
     * Value: 对应的缓存适配器实例
     */
    private final Map<Long, MqttClientBufferAdapter> adapterMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化所有 MqttClient 对应的缓存适配器（自动启动）
     */
    @DependsOn("forwardMqttClientService")
    @PostConstruct
    public void init() {
        if (config.isEnable()) {
            config.getMqttServerGatewayList().forEach(mqttServerGateway -> {
                if (mqttServerGateway.isEnable()) {
                    addMqttClientAdapter(mqttServerGateway);
                }
            });
        }
        log.info("BufferForwardMqttClientAdapter 初始化完成，共创建 {} 个缓存适配器", adapterMap.size());
    }

    /**
     * 添加 MqttClient 缓存适配器（支持自动启动和手动加入）
     * 会检查对应的 MqttClient 是否已在 ForwardMqttClientService 中创建
     *
     * @param mqttServerGateway MQTT 服务器网关配置
     */
    public void addMqttClientAdapter(MqttServerGateway mqttServerGateway) {
        if (adapterMap.containsKey(mqttServerGateway.getId())) {
            log.warn("MqttClient [id={}, name={}] 缓存适配器已存在，跳过创建", 
                    mqttServerGateway.getId(), mqttServerGateway.getServerName());
            return;
        }
        
        MqttClient mqttClient = forwardMqttClientService.getMqttClient(mqttServerGateway.getId());
        if (mqttClient == null) {
            // 创建 MqttClient
            mqttClient = forwardMqttClientService.startWithConnect(mqttServerGateway);
        }

        MqttClientBufferAdapter adapter = new MqttClientBufferAdapter(mqttServerGateway);
        adapter.start();
        adapterMap.put(mqttServerGateway.getId(), adapter);
        log.debug("为 MqttClient [id={}, name={}] 创建缓存适配器",
                mqttServerGateway.getId(), mqttServerGateway.getServerName());
    }

    /**
     * 移除 MqttClient 缓存适配器
     *
     * @param mqttClientId MqttClient ID
     */
    public void removeMqttClientAdapter(Long mqttClientId) {
        MqttClientBufferAdapter adapter = adapterMap.remove(mqttClientId);
        if (adapter != null) {
            // TODO 停止 MqttClient
            adapter.shutdown();
            log.info("移除 MqttClient [id={}] 缓存适配器", mqttClientId);
        }
    }


    /**
     * 向指定的 MqttClient 推送数据（带缓存）
     * 根据设备ID从全局缓存中获取设备信息
     *
     * @param deviceCode 设备ID
     * @param values   待推送的数据值列表
     */
    public void push(String deviceCode, List<List<String>> values) {
        if (deviceCode == null || values == null || values.isEmpty()) {
            log.warn("推送数据失败：设备ID或数据为空");
            return;
        }

        DeviceInfo deviceInfo = deviceInfoManager.getDeviceInfo(deviceCode);
        if (deviceInfo == null) {
            log.warn("推送数据失败：未找到设备信息，deviceCode={}", deviceCode);
            return;
        }

        MqttClientBufferAdapter adapter = adapterMap.get(deviceInfoManager.getMqttClientId(deviceCode));
        if (adapter == null) {
            log.warn("推送数据失败：未找到 MqttClient [id={}] 对应的缓存适配器", deviceInfoManager.getMqttClientId(deviceCode));
            return;
        }

        adapter.push(deviceCode, values);
    }

    /**
     * 强制发送指定 MqttClient 的指定设备的所有缓存数据
     *
     * @param deviceId     设备编号
     */
    public void flush(String deviceId) {
        MqttClientBufferAdapter adapter = adapterMap.get(deviceInfoManager.getMqttClientId(deviceId));
        if (adapter == null) {
            log.warn("强制发送失败：未找到 MqttClient [id={}] 对应的缓存适配器", deviceInfoManager.getMqttClientId(deviceId));
            return;
        }
        adapter.flush(deviceId);
    }

    /**
     * 强制发送指定 MqttClient 的所有缓存数据
     *
     * @param mqttClientId MqttClient ID
     */
    public void flushAll(Long mqttClientId) {
        MqttClientBufferAdapter adapter = adapterMap.get(mqttClientId);
        if (adapter == null) {
            log.warn("强制发送失败：未找到 MqttClient [id={}] 对应的缓存适配器", mqttClientId);
            return;
        }
        adapter.flushAll();
    }

    /**
     * 强制发送所有 MqttClient 的所有缓存数据
     */
    public void flushAllClients() {
        adapterMap.values().forEach(MqttClientBufferAdapter::flushAll);
    }

    /**
     * 获取指定 MqttClient 的缓存适配器
     *
     * @param mqttClientId MqttClient ID
     * @return 缓存适配器实例
     */
    public MqttClientBufferAdapter getAdapter(Long mqttClientId) {
        return adapterMap.get(mqttClientId);
    }

    /**
     * 清理资源
     */
    @PreDestroy
    public void shutdown() {
        log.info("BufferForwardMqttClientAdapter 开始关闭...");
        adapterMap.values().forEach(MqttClientBufferAdapter::shutdown);
        adapterMap.clear();
        deviceInfoManager.clear();
        log.info("BufferForwardMqttClientAdapter 已关闭");
    }

    /**
     * 单个 MqttClient 的缓存适配器
     * 每个 MqttClient 对应一个独立的缓存适配器实例
     */
    @RequiredArgsConstructor
    public class MqttClientBufferAdapter {
        private final MqttServerGateway mqttServerGateway;

        /**
         * 按设备编号分组的数据缓存
         * Key: 设备ID
         * Value: 数据值列表
         */
        private final Map<String, List<List<String>>> deviceDataBuffer = new ConcurrentHashMap<>();

        /**
         * 每个设备的最后发送时间
         */
        private final Map<String, Long> lastSendTimes = new ConcurrentHashMap<>();

        private final Long MAX_BUFFER_SIZE = 100L;  // 最大缓存条数
        private Long timerId;
        private Long sendInterval;

        /**
         * 启动缓存适配器
         */
        public void start() {
            // 从配置中获取发送间隔，如果没有配置则使用默认值 500ms
            this.sendInterval = mqttServerGateway.getSendIntervalMs() != null 
                    ? mqttServerGateway.getSendIntervalMs() 
                    : 500L;
            
            // 启动定时发送任务
            startScheduledSendTask();
            
            log.debug("MqttClient [id={}, name={}] 缓存适配器启动完成，最大缓存: {} 条，发送间隔: {}ms",
                    mqttServerGateway.getId(), mqttServerGateway.getServerName(), MAX_BUFFER_SIZE, sendInterval);
        }

        /**
         * 添加数据到缓存
         *
         * @param deviceId 设备ID
         * @param values   待缓存的数据值列表
         */
        private void push(String deviceId, List<List<String>> values) {
            // 确保原子性的更新缓存
            deviceDataBuffer.compute(deviceId, (id, buffer) -> {
                if (buffer == null) {
                    buffer = new ArrayList<>();
                }

                // 因为对同一个 key 的 compute 是原子的，所以这里的 addAll 是线程安全的
                buffer.addAll(values);

                // 如果缓存数据达到阈值，立即发送
                if (buffer.size() >= MAX_BUFFER_SIZE) {
                    vertx.runOnContext(v -> sendAndClearBuffer(deviceId));
                }
                return buffer;
            });
        }

        /**
         * 启动定时发送任务
         */
        private void startScheduledSendTask() {
            timerId = vertx.setPeriodic(sendInterval, id -> {
                // 确保定时器回调中的代码是非阻塞的
                long currentTime = System.currentTimeMillis();
                

                // TODO 优化为并发方案
                // TODO 目前设计只支持少量设备（少于100） 如果设备增多，则无法使用这种方式推送
                deviceDataBuffer.keySet().forEach(deviceId -> {
                    Long lastSendTime = lastSendTimes.getOrDefault(deviceId, 0L);
                    if (currentTime - lastSendTime >= sendInterval) {
                        sendAndClearBuffer(deviceId);
                    }
                });
            });

            log.debug("MqttClient [id={}] 定时发送任务已启动，检查间隔: {} ms", 
                    mqttServerGateway.getId(), sendInterval);
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
            sendAndClearBuffer(deviceId);
        }

        /**
         * 强制发送所有设备的缓存数据
         */
        public void flushAll() {
            // 创建快照，避免并发修改异常
            new ArrayList<>(deviceDataBuffer.keySet()).forEach(this::sendAndClearBuffer);
        }

        /**
         * 发送指定设备的缓存数据
         */
        private void sendAndClearBuffer(String deviceId) {
            // 确保线程安全的移除和获取缓存
            List<List<String>> bufferToSend = deviceDataBuffer.remove(deviceId);

            if (bufferToSend == null || bufferToSend.isEmpty()) {
                return;
            }

            // 从全局设备信息缓存中获取设备信息
            DeviceInfo deviceInfo = deviceInfoManager.getDeviceInfo(deviceId);
            if (deviceInfo == null) {
                log.warn("发送缓存数据失败：未找到设备信息，deviceId={}", deviceId);
                // 将数据放回缓存
                deviceDataBuffer.put(deviceId, bufferToSend);
                return;
            }

            lastSendTimes.put(deviceId, System.currentTimeMillis());

            try {
                // 构造 MqttData
                MqttData dataToSend = MqttData.builder()
                        .deviceId(deviceId)
                        .deviceType(deviceInfo.getDeviceType() != null ? deviceInfo.getDeviceType().name() : "UNKNOWN")
                        .value(bufferToSend)
                        .sampleTime(System.currentTimeMillis())
                        .build();

                final MqttPubProtocol protocol = new MqttPubProtocol(
                        mqttServerGateway.getDataTag(), 
                        Collections.singletonList(dataToSend)
                );

                // 序列化为 JSON 字符串
                String jsonData = objectMapper.writeValueAsString(protocol);

                // 调用 ForwardMqttClientService 发送数据
                forwardMqttClientService.sendData(mqttServerGateway.getId(), jsonData);

                log.debug("MqttClient [id={}] 发送缓存数据成功，设备: {}，数据条数: {}", 
                        mqttServerGateway.getId(), deviceId, bufferToSend.size());
            } catch (Exception e) {
                log.error("MqttClient [id={}] 发送缓存数据失败，设备: {}，数据条数: {}", 
                        mqttServerGateway.getId(), deviceId, bufferToSend.size(), e);
            }
        }

        /**
         * 清理资源
         */
        public void shutdown() {
            if (timerId != null) {
                // 取消 Vert.x 定时器
                vertx.cancelTimer(timerId);
                log.info("MqttClient [id={}] 缓存适配器定时器已取消", mqttServerGateway.getId());
            }

            // 发送所有剩余数据
            flushAll();
            
            log.info("MqttClient [id={}] 缓存适配器已关闭", mqttServerGateway.getId());
        }
    }
}

