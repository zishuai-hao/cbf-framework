package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * RHS设备数据（温湿度监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class RHSDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.RHS;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.RHS;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("RHS设备需要2个参数：温度, 湿度");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String temperature, String humidity) {
        return Arrays.asList(temperature, humidity);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String temperature, String humidity) {
        return format(temperature, humidity);
    }

}
