package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * VIE设备数据（地震监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class VIEDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.VIE;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VIE;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("VIE设备需要3个参数：X轴, Y轴, Z轴");
        }
        return Arrays.asList(params[0], params[1], params[2]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String xValue, String yValue, String zValue) {
        return Arrays.asList(xValue, yValue, zValue);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String xValue, String yValue, String zValue) {
        return format(xValue, yValue, zValue);
    }

}
