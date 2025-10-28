package com.company.cbf.starter.data.entity.device;

import java.util.Collections;
import java.util.List;

/**
 * TMP设备数据（结构温度监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class TMPDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.TMP;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.TMP;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("TMP设备需要1个参数：温度值");
        }
        return Collections.singletonList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String temperature) {
        return Collections.singletonList(temperature);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String temperature) {
        return format(temperature);
    }

}
