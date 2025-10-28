package com.company.cbf.starter.data.entity.device;

import java.util.Collections;
import java.util.List;

/**
 * VSB设备数据（能见度监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class VSBDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.VSB;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VSB;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("VSB设备需要1个参数：大气能见度");
        }
        return Collections.singletonList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String visibility) {
        return Collections.singletonList(visibility);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String visibility) {
        return format(visibility);
    }

}
