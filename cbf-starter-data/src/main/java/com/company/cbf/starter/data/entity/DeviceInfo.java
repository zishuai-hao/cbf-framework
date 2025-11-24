package com.company.cbf.starter.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.company.cbf.starter.data.constant.NetworkGatewayType;
import com.company.cbf.starter.data.service.forward.device.DeviceType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;

import java.io.Serializable;

/**
 * 本地设备信息实体类
 *
 * @author zs
 * @since 2025-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceInfo implements Serializable {

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点编号
     */
    @TableField("device_code")
    private String deviceCode;

    /**
     * 监测内容
     */
    @TableField("monitoring_content")
    private String monitoringContent;

    /**
     * 设备类型
     */
    @TableField(value = "device_type", typeHandler = EnumTypeHandler.class)
    private DeviceType deviceType;

    /**
     * 用户名称
     */
    @TableField("username")
    private String username;

    /**
     * 设备接入方式
     *
     */
    @TableField(value = "network_gateway_type", typeHandler = EnumTypeHandler.class)
    private NetworkGatewayType networkGatewayType;

    /**
     * 采集频率频率（hz）
     * 数字：1、5、10、20、50、100 表示采集频率
     * 字符串：1/300 表示每300秒采集一次
     */
    @TableField("frequency")
    private String frequency;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private String updateTime;

    /**
     * 是否启用 (0-禁用, 1-启用)
     */
    @TableField("enable")
    private boolean enable;

    /**
     * 最后连接时间
     */
    @TableField("last_connect_time")
    private String lastConnectTime;

    /**
     * 最后数据时间
     */
    @TableField("last_data_time")
    private String lastDataTime;
}


