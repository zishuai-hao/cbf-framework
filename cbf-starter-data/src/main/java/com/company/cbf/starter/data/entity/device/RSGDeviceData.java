package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * RSG设备数据（应变监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class RSGDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.RSG;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.RSG;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("RSG设备需要2个参数：应变值, 温度值");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String strain, String temperature) {
        return Arrays.asList(strain, temperature);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String strain, String temperature) {
        return format(strain, temperature);
    }

}
