package com.company.cbf.starter.data.entity.device;

import java.util.List;

/**
 * 设备数据提供者接口
 * 所有设备数据类都应该实现此接口
 *
 * @author hzs
 * @date 2023/12/02
 */
public interface DeviceDataProvider {
    
    /**
     * 获取设备类型
     * @return 设备类型
     */
    DeviceType getDeviceType();
    
    /**
     * 格式化设备数据
     * @param params 参数数组
     * @return 格式化后的数据列表
     */
    List<String> format(String... params);
}
