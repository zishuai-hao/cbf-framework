package com.company.cbf.starter.data.entity.device;

import lombok.Getter;

/**
 * 监测类型枚举
 * 每个枚举值都必须提供对应的设备数据处理器实现
 *
 * @author hzs
 * @date 2023/12/02
 */
@Getter
public enum DeviceType {

    // 环境
    RHS("温湿度监测") { // 温度, 湿度
        @Override
        public DeviceDataProvider createDataProvider() {
            return new RHSDeviceData();
        }
    },
    PWS("雨量监测") { // 降雨量
        @Override
        public DeviceDataProvider createDataProvider() {
            return new PWSDeviceData();
        }
    },
    WLV("水位监测") { // 水位, 流速
        @Override
        public DeviceDataProvider createDataProvider() {
            return new WLVDeviceData();
        }
    },
    VSB("能见度监测") { // 大气能见度
        @Override
        public DeviceDataProvider createDataProvider() {
            return new VSBDeviceData();
        }
    },

    // 作用
    HSD("车辆荷载监测") { // 荷载数据
        @Override
        public DeviceDataProvider createDataProvider() {
            return new HSDDeviceData();
        }
    },
    UAN("风速风向监测") { // 风速, 风向, 风攻角
        @Override
        public DeviceDataProvider createDataProvider() {
            return new UANDeviceData();
        }
    },
    TMP("结构温度监测") { // 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new TMPDeviceData();
        }
    },
    VID("船舶撞击监测") { // 横桥向, 纵桥向, 竖向
        @Override
        public DeviceDataProvider createDataProvider() {
            return new VIDDeviceData();
        }
    },
    VIE("地震监测") { // X轴, Y轴, Z轴
        @Override
        public DeviceDataProvider createDataProvider() {
            return new VIEDeviceData();
        }
    },

    // 结构响应
    DIS("位移监测") { // 位移值, 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new DISDeviceData();
        }
    },
    INC("转角监测") { // 横桥向, 纵桥向, 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new INCDeviceData();
        }
    },
    RSG("应变监测") { // 应变值, 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new RSGDeviceData();
        }
    },
    VIC("索力监测") { // 索力值, 基频
        @Override
        public DeviceDataProvider createDataProvider() {
            return new VICDeviceData();
        }
    },
    VIB("振动监测") { // 加速度数据
        @Override
        public DeviceDataProvider createDataProvider() {
            return new VIBDeviceData();
        }
    },
    GNSS("结构空间变形监测") { // X轴, Y轴, Z轴
        @Override
        public DeviceDataProvider createDataProvider() {
            return new GNSSDeviceData();
        }
    },
    HPT("挠度监测") { // 位移值, 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new HPTDeviceData();
        }
    },

    // 结构变化
    AND("位移变化监测") { // 横桥向, 纵桥向, 竖向
        @Override
        public DeviceDataProvider createDataProvider() {
            return new ANDDeviceData();
        }
    },
    CRK("裂缝监测") { // 裂缝值, 温度值
        @Override
        public DeviceDataProvider createDataProvider() {
            return new CRKDeviceData();
        }
    },
    SCO("基础冲刷监测") { // 深度
        @Override
        public DeviceDataProvider createDataProvider() {
            return new SCODeviceData();
        }
    };

    private final String description;

    DeviceType(String description) {
        this.description = description;
    }

    /**
     * 创建对应的设备数据处理器
     * 每个枚举值都必须实现此方法，确保编译时检查
     *
     * @return 设备数据处理器实例
     */
    public abstract DeviceDataProvider createDataProvider();
}
