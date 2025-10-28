package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * VIB设备数据（振动监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class VIBDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.VIB;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VIB;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("VIB设备需要1个参数：加速度数据");
        }
        return Arrays.asList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String accelerationData) {
        return Arrays.asList(accelerationData);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String accelerationData) {
        return format(accelerationData);
    }

}
