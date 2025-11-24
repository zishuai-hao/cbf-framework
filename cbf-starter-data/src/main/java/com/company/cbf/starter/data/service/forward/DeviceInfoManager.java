package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.entity.DeviceInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备信息管理器
 * 负责管理全局设备信息缓存和设备与 MQTT 客户端的映射关系
 * 
 * @author hzs
 * @date 2025/11/15
 */
@Slf4j
public class DeviceInfoManager {
    
    /**
     * 全局设备信息缓存
     * Key: 设备ID
     * Value: 设备信息
     */
    private final Map<String, DeviceInfo> deviceInfoCache = new ConcurrentHashMap<>();

    /**
     * 设备与 MQTT 客户端的映射关系
     * Key: 设备ID
     * Value: MQTT 客户端 ID
     */
    private final Map<String, Long> deviceMqttClientIdMap = new ConcurrentHashMap<>();

    /**
     * 注册设备信息到全局缓存
     *
     * @param deviceInfo   设备信息
     * @param mqttClientId MQTT 客户端 ID
     */
    public void registerDevice(DeviceInfo deviceInfo, Long mqttClientId) {
        if (deviceInfo == null || deviceInfo.getDeviceCode() == null) {
            log.warn("注册设备信息失败：设备信息或设备ID为空");
            return;
        }
        deviceInfoCache.put(deviceInfo.getDeviceCode(), deviceInfo);
        deviceMqttClientIdMap.put(deviceInfo.getDeviceCode(), mqttClientId);
        log.debug("注册设备信息成功：deviceId={}, deviceType={}, mqttClientId={}", 
                deviceInfo.getDeviceCode(), deviceInfo.getDeviceType(), mqttClientId);
    }

    /**
     * 批量注册设备信息到全局缓存
     *
     * @param deviceInfoList 设备信息列表
     * @param mqttClientId   MQTT 客户端 ID
     */
    public void registerDevices(List<DeviceInfo> deviceInfoList, Long mqttClientId) {
        if (deviceInfoList == null || deviceInfoList.isEmpty()) {
            return;
        }
        deviceInfoList.forEach(deviceInfo -> registerDevice(deviceInfo, mqttClientId));
        log.info("批量注册设备信息成功，共注册 {} 个设备", deviceInfoList.size());
    }

    /**
     * 从全局缓存中移除设备信息
     *
     * @param deviceId 设备ID
     */
    public void unregisterDevice(String deviceId) {
        DeviceInfo removed = deviceInfoCache.remove(deviceId);
        deviceMqttClientIdMap.remove(deviceId);
        if (removed != null) {
            log.debug("移除设备信息成功：deviceId={}", deviceId);
        }
    }

    /**
     * 获取设备信息
     *
     * @param deviceId 设备ID
     * @return 设备信息，如果不存在返回 null
     */
    public DeviceInfo getDeviceInfo(String deviceId) {
        return deviceInfoCache.get(deviceId);
    }

    /**
     * 获取设备对应的 MQTT 客户端 ID
     *
     * @param deviceId 设备ID
     * @return MQTT 客户端 ID，如果不存在返回 null
     */
    public Long getMqttClientId(String deviceId) {
        return deviceMqttClientIdMap.get(deviceId);
    }

    /**
     * 检查设备是否已注册
     *
     * @param deviceId 设备ID
     * @return true 如果设备已注册，否则返回 false
     */
    public boolean isDeviceRegistered(String deviceId) {
        return deviceInfoCache.containsKey(deviceId);
    }

    /**
     * 获取所有已注册的设备数量
     *
     * @return 设备数量
     */
    public int getDeviceCount() {
        return deviceInfoCache.size();
    }

    /**
     * 清空所有设备信息
     */
    public void clear() {
        int count = deviceInfoCache.size();
        deviceInfoCache.clear();
        deviceMqttClientIdMap.clear();
        log.info("清空所有设备信息，共清空 {} 个设备", count);
    }
}

