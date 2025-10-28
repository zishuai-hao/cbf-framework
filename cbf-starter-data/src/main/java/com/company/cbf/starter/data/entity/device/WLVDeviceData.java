package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * WLV设备数据（水位监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class WLVDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.WLV;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.WLV;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("WLV设备需要2个参数：水位, 流速");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String waterLevel, String flowRate) {
        return Arrays.asList(waterLevel, flowRate);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String waterLevel, String flowRate) {
        return format(waterLevel, flowRate);
    }

}
