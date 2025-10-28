package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * VID设备数据（船舶撞击监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class VIDDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.VID;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VID;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 3) {
            throw new IllegalArgumentException("VID设备需要3个参数：横桥向, 纵桥向, 竖向");
        }
        return Arrays.asList(params[0], params[1], params[2]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String crossBridgeDirection, String longitudinalDirection, String verticalDirection) {
        return Arrays.asList(crossBridgeDirection, longitudinalDirection, verticalDirection);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String crossBridgeDirection, String longitudinalDirection, String verticalDirection) {
        return format(crossBridgeDirection, longitudinalDirection, verticalDirection);
    }

}
