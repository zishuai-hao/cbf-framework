package com.company.cbf.starter.data.service.forward.repub;

import com.company.cbf.starter.data.service.forward.device.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hzs
 * @date 2025/11/09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReDevice {
    private String deviceId;
    private DeviceType deviceType;
}
