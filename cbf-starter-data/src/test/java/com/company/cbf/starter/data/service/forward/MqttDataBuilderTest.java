package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import com.company.cbf.starter.data.entity.device.DeviceType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author hzs
 * @date 2025/10/26
 */
public class MqttDataBuilderTest {
    /**
     * 测试MqttDataBuilder的构建
     */
    @Test
    void testWithTimeBuilder() {
        final MqttData data = new MqttDataBuilder(DeviceType.DIS)
                .sampleTime(System.currentTimeMillis())
                .monitoringCode("DIS001")
                .addData("2025-10-26 10:00:00.000", "10.0", "20.0")
                .build();
        final MqttPubProtocol zd001 = new MqttPubProtocol("ZD001", Collections.singletonList(data));
        System.out.println(zd001);
    }

    @Test
    void testCurrentTimeBuilder() {
        final MqttData data = new MqttDataBuilder(DeviceType.DIS)
                .currentTime()
                .monitoringCode("DIS001")
                .addCurrentTimeData("10.0", "20.0")
                .build();
        final MqttPubProtocol zd001 = new MqttPubProtocol("ZD001", Collections.singletonList(data));
        System.out.println(zd001);
    }
}
