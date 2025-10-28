package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * VIC设备数据（索力监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class VICDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.VIC;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VIC;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("VIC设备需要2个参数：索力值, 基频");
        }
        return Arrays.asList(params[0], params[1]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String cableForce, String fundamentalFrequency) {
        return Arrays.asList(cableForce, fundamentalFrequency);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String cableForce, String fundamentalFrequency) {
        return format(cableForce, fundamentalFrequency);
    }

}
