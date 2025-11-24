package com.company.cbf.starter.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 该类作为MqttServerGateway的抽象类，客户端需要继承该类然后构建sql语句
 */
@Data
public abstract class MqttServerGateway {
    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 名称
     */
    @TableField("server_name")
    private String serverName;
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;
    /**
     * MQTT服务器地址
     */
    @TableField("url")
    private String url;
    /**
     * MQTT服务器端口
     */
    @TableField("port")
    private Integer port;
    /**
     * MQTT服务器主题
     */
    @TableField("topic")
    private String topic;
    /**
     * MQTT服务器用户名
     */
    @TableField("username")
    private String username;
    /**
     * MQTT服务器密码
     */
    @TableField("password")
    private String password;
    /**
     * MQTT服务器数据标识
     */
    @TableField("data_tag")
    private String dataTag;
    /**
     * 是否启用
     */
    @TableField("enable")
    private boolean enable;
    /**
     * MQTT服务器客户端ID
     */
    @TableField("client_id")
    private String clientId; // 新增，允许配置 client ID
    /**
     * MQTT服务器重连间隔
     */
    @TableField("reconnect_interval")
    private Integer reconnectInterval; // 新增，允许配置重连间隔
    /**
     * MQTT服务器发送间隔
     */
    @TableField("send_interval_ms")
    private Long sendIntervalMs; // 新增，允许配置发送间隔

}
