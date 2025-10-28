package com.company.cbf.starter.data.entity.device;

import java.util.Collections;
import java.util.List;

/**
 * SCO设备数据（基础冲刷监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class SCODeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.SCO;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.SCO;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("SCO设备需要1个参数：深度");
        }
        return Collections.singletonList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String depth) {
        return Collections.singletonList(depth);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String depth) {
        return format(depth);
    }

}
