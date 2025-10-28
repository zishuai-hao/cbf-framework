package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * UAN设备数据（风速风向监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class UANDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.UAN;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.UAN;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("UAN设备需要3个参数：风速, 风向, 风攻角");
        }
        return Arrays.asList(params[0], params[1], params[2]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String windSpeed, String windDirection, String windAttackAngle) {
        return Arrays.asList(windSpeed, windDirection, windAttackAngle);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String windSpeed, String windDirection, String windAttackAngle) {
        return format(windSpeed, windDirection, windAttackAngle);
    }

}
