package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * PWS设备数据（雨量监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class PWSDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.PWS;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.PWS;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("PWS设备需要1个参数：降雨量");
        }
        return Arrays.asList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String rainfall) {
        return Arrays.asList(rainfall);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String rainfall) {
        return format(rainfall);
    }

}
