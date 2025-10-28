package com.company.cbf.starter.data.entity.device;

import java.util.Arrays;
import java.util.List;

/**
 * HSD设备数据（车辆荷载监测）
 *
 * @author hzs
 * @date 2023/12/02
 */
public class HSDDeviceData implements DeviceDataProvider {
    
    /**
     * 设备类型常量（保持向后兼容）
     */
    public static final DeviceType DEVICE_TYPE = DeviceType.HSD;
    
    @Override
    public DeviceType getDeviceType() {
        return DeviceType.HSD;
    }
    
    @Override
    public List<String> format(String... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("HSD设备需要1个参数：荷载数据");
        }
        return Arrays.asList(params[0]);
    }

    /**
     * 便捷方法（保持向后兼容）
     */
    public static List<String> format(String loadData) {
        return Arrays.asList(loadData);
    }
    
    /**
     * 实例方法版本
     */
    public List<String> formatData(String loadData) {
        return format(loadData);
    }

}
