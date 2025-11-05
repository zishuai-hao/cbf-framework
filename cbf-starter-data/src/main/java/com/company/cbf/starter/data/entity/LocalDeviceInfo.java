//package com.company.cbf.starter.data.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.company.cbf.starter.data.service.forward.device.DeviceType;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//import org.apache.ibatis.type.EnumTypeHandler;
//
//import java.io.Serializable;
//
///**
// * 本地设备信息实体类
// *
// * @author zs
// * @since 2025-09-22
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
//@TableName("local_device_info")
//public class LocalDeviceInfo implements Serializable {
//
//    /**
//     * 主键ID（自增）
//     */
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * Excel序号
//     */
//    @TableField("sequence_number")
//    private Integer sequenceNumber;
//
//    /**
//     * 站点编号
//     */
//    @TableField("device_id")
//    private String deviceId;
//
//    /**
//     * 监测内容
//     */
//    @TableField("monitoring_content")
//    private String monitoringContent;
//
//    /**
//     * 设备类型
//     */
//    @TableField(value = "device_type", typeHandler = EnumTypeHandler.class)
//    private DeviceType deviceType;
//
//    /**
//     * 位置
//     */
//    @TableField("location")
//    private String location;
//
//    /**
//     * 断面
//     */
//    @TableField("cross_section")
//    private String crossSection;
//
//    /**
//     * 地址
//     */
//    @TableField("address")
//    private String address;
//
//    /**
//     * 通道
//     */
//    @TableField("channel")
//    private Integer channel;
//
//    /**
//     * IP地址
//     */
//    @TableField("ip")
//    private String ip;
//
//    /**
//     * 端口
//     */
//    @TableField("port")
//    private Integer port;
//
//    /**
//     * 用户名称
//     */
//    @TableField("username")
//    private String username;
//
//    /**
//     * 密码
//     */
//    @TableField("password")
//    private String password;
//
//    /**
//     * 频率（hz）
//     */
//    @TableField("frequency")
//    private String frequency;
//
//    /**
//     * 备注
//     */
//    @TableField("remarks")
//    private String remarks;
//
//    /**
//     * 创建时间
//     */
//    @TableField("create_time")
//    private String createTime;
//
//    /**
//     * 更新时间
//     */
//    @TableField("update_time")
//    private String updateTime;
//
//    /**
//     * 是否启用 (0-禁用, 1-启用)
//     */
//    @TableField("enabled")
//    private Integer enabled;
//
//    /**
//     * 设备状态 (0-离线, 1-在线, 2-故障)
//     */
//    @TableField("device_status")
//    private Integer deviceStatus;
//
//    /**
//     * 最后连接时间
//     */
//    @TableField("last_connect_time")
//    private String lastConnectTime;
//
//    /**
//     * 最后数据时间
//     */
//    @TableField("last_data_time")
//    private String lastDataTime;
//}
//
//
