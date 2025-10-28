package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.device.DeviceType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * MQTT数据构建器
 * 负责构建单个MQTT数据对象
 *
 * @author hzs
 * @date 2023/12/02
 */
public class MqttDataBuilder {

    private Long sampleTime;
    private String monitoringCode;
    private final DeviceType deviceType;
    private final List<List<String>> value = new ArrayList<>();

    public MqttDataBuilder(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 设置发信时间戳
     */
    public MqttDataBuilder sampleTime(Long sampleTime) {
        this.sampleTime = sampleTime;
        return this;
    }

    /**
     * 设置当前时间戳为发信时间戳
     */
    public MqttDataBuilder currentTime() {
        this.sampleTime = System.currentTimeMillis();
        return this;
    }

    /**
     * 设置传感器编码
     */
    public MqttDataBuilder monitoringCode(String monitoringCode) {
        this.monitoringCode = monitoringCode;
        return this;
    }

    /**
     * 添加数据（使用 DeviceType 的格式化器）
     *
     * @param timeStr       时间字符串
     * @param formattedData 已格式化的数据列表
     */
    public MqttDataBuilder addData(String timeStr, String... params) {
        List<String> formattedData = deviceType.createDataProvider().format(params);
        List<String> dataPoint = new ArrayList<>();
        dataPoint.add(timeStr);
        dataPoint.addAll(formattedData);
        this.value.add(dataPoint);
        return this;
    }

    /**
     * 添加当前时间的数据
     *
     * @param formattedData 已格式化的数据列表
     */
    public MqttDataBuilder addCurrentTimeData(String... formattedData) {
        String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return addData(currentTimeStr, formattedData);
    }

    /**
     * 构建MqttData实例
     */
    public MqttData build() {
        validate();

        return MqttData.builder()
                .sampleTime(sampleTime)
                .deviceId(monitoringCode)
                .deviceType(deviceType.name())
                .value(value)
                .build();
    }

    /**
     * 参数校验
     */
    private void validate() {
        if (sampleTime == null) {
            throw new IllegalArgumentException("时间戳不能为空");
        }
        if (monitoringCode == null || monitoringCode.trim().isEmpty()) {
            throw new IllegalArgumentException("传感器编码不能为空");
        }
        if (deviceType == null) {
            throw new IllegalArgumentException("监测类型不能为空");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("实时数据不能为空");
        }
    }
}
