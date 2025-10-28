package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * INC设备数据（转角监测）
 *
 * @author hzs
 */
public class INCDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.INC;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.INC;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("INC设备需要3个参数：横桥向, 纵桥向, 温度值");
        }
        return Arrays.asList(params[0], params[1], params[2]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String crossBridgeDirection, String longitudinalDirection, String temperature) {
        return Arrays.asList(crossBridgeDirection, longitudinalDirection, temperature);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String crossBridgeDirection, String longitudinalDirection, String temperature) {
        return format(crossBridgeDirection, longitudinalDirection, temperature);
    }

}
