package com.company.cbf.starter.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MQTT数据实体类
 * 包含单个传感器的监测数据
 * 
 * @author hzs
 * @date 2023/12/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttData {
    
    /**
     * 时间戳（毫秒）
     */
    @JsonProperty("sample_time")
    private Long sampleTime;
    
    /**
     * 传感器编码
     * 需要保证全系统编码唯一，且和《桥梁传感器信息模板.xlsx》保持一致
     */
    @JsonProperty("device_id")
    private String deviceId;
    
    /**
     * 监测类型
     * 具体参照"监测类型说明"
     */
    @JsonProperty("device_type")
    private String deviceType;
    
    /**
     * 实时数据
     * 具体参照"实时数据说明"
     * 格式：[["2023-08-25 10:24:25.020","19.5"]]
     */
    @JsonProperty("value")
    private List<List<String>> value;
}
