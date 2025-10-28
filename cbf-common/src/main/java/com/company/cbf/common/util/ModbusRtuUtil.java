package com.company.cbf.common.util;

public class ModbusRtuUtil {

    /**
     * 验证CRC（默认使用正常CRC）
     */
    public static boolean verifyCrc(byte[] data) {
        return verifyCrc(data, false);
    }

    /**
     * 验证CRC
     * @param data 数据
     * @param isReflectedCrc 是否使用翻转CRC（高低字节互换）
     * @return 验证结果
     */
    public static boolean verifyCrc(byte[] data, boolean isReflectedCrc) {
        if (data == null || data.length < 3) {
            return false;
        }
        int len = data.length;
        int expected = ((data[len - 1] & 0xFF) << 8) | (data[len - 2] & 0xFF);
        int actual = crc16(data, len - 2, isReflectedCrc);
        return expected == actual;
    }

    /**
     * Modbus RTU CRC16 (poly 0xA001, init 0xFFFF, little-endian)
     * @param data 数据
     * @param length 数据长度
     * @return CRC值
     */
    public static int crc16(byte[] data, int length) {
        return crc16(data, length, false);
    }

    /**
     * Modbus RTU CRC16 (poly 0xA001, init 0xFFFF, little-endian)
     * @param data 数据
     * @param length 数据长度
     * @param isReflectedCrc 是否使用翻转CRC（高低字节互换）
     * @return CRC值
     */
    public static int crc16(byte[] data, int length, boolean isReflectedCrc) {
        int crc = 0xFFFF;
        for (int i = 0; i < length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc = (crc >>> 1);
                }
            }
        }
        crc = crc & 0xFFFF;

        // 如果需要翻转CRC，将高低字节互换
        if (isReflectedCrc) {
            crc = ((crc & 0xFF) << 8) | ((crc >> 8) & 0xFF);
        }

        return crc;
    }
}
