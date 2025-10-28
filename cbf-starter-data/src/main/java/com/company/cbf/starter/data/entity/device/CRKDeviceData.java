package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * CRK设备数据（裂缝监测）
 *
 * @author hzs
 */
public class CRKDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.CRK;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.CRK;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("CRK设备需要2个参数：裂缝值, 温度值");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String crackValue, String temperature) {
        return Arrays.asList(crackValue, temperature);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String crackValue, String temperature) {
        return format(crackValue, temperature);
    }

}
