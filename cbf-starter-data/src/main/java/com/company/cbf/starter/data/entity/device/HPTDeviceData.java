package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * HPT设备数据（挠度监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class HPTDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.HPT;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.HPT;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("HPT设备需要2个参数：位移值, 温度值");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String displacement, String temperature) {
        return Arrays.asList(displacement, temperature);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String displacement, String temperature) {
        return format(displacement, temperature);
    }

}
