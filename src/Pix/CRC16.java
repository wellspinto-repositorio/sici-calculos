package Pix;

/**
 * CRC16_CCITT: Polynomial x16+x12+x5+1 (0x1021), initial value 0x0000, low bit first, high bit after, result is exclusive to 0x0000
   * CRC16_CCITT_FALSE: Polynomial x16+x12+x5+1 (0x1021), initial value 0xFFFF, low bit is after, high bit is first, result is exclusive to 0x0000
   * CRC16_XMODEM: Polynomial x16+x12+x5+1 (0x1021), initial value 0x0000, low bit after, high bit first, result is XOR0
   * CRC16_X25: Polynomial x16+x12+x5+1 (0x1021), initial value 0xffff, low bit first, high bit after, result is XORIF 0xFFFF
   * CRC16_MODBUS: Polynomial x16+x15+x2+1 (0x8005), initial value 0xFFFF, low bit first, high bit after, result is X2000000 exclusive OR
   * CRC16_IBM: Polynomial x16+x15+x2+1 (0x8005), initial value 0x0000, low bit first, high bit after, result is XOR0
   * CRC16_MAXIM: Polynomial x16+x15+x2+1 (0x8005), initial value 0x0000, low bit first, high bit after, result is XORIF 0xFFFF
   * CRC16_USB: Polynomial x16+x15+x2+1 (0x8005), initial value 0xFFFF, low bit first, high bit after, result is XORIF 0xFFFF
 * <p>
   * (1), preset a 16-bit register to hexadecimal FFFF (that is, all 1), call this register a CRC register;
   * (2), the first 8-bit binary data (the first byte of the communication information frame) is different from the lower 8 bits of the 16-bit CRC register, the result is placed in the CRC register, the upper eight bits of data are not Change
   * (3), shift the contents of the CRC register one bit to the right (toward the low position) to fill the highest bit with 0, and check the shifted out bit after the right shift;
   * (4) If the shift bit is 0: repeat step 3 (shift one bit right again); if the shift bit is 1, the CRC register is XORed with the polynomial A001 (1010 0000 0000 0001);
   * (5), repeat steps 3 and 4 until the right shift 8 times, so that the entire 8-bit data is processed;
   * (6), repeat steps 2 to 5, and process the next byte of the communication information frame;
   * (7), after all the bytes of the communication information frame are calculated according to the above steps, the high and low bytes of the obtained 16-bit CRC register are exchanged;
   * (8), the final CRC register content is: CRC code.
 * <p>
 * The polynomial 0xA001 in the above calculation step is the result of 0x8005 bitwise reversal.
   * 0x8408 is the result of 0x1021 bitwise reversal.
   * Online verification tool
 * http://www.ip33.com/crc.html
 * https://blog.csdn.net/htmlxx/article/details/17369105
 * <p>
 */
public class CRC16 {
 
    /**
           * CRC16_CCITT: Polynomial x16+x12+x5+1 (0x1021), initial value 0x0000, low bit first, high bit after, result is exclusive to 0x0000
           * 0x8408 is the result of 0x1021 bitwise reversal.
     * @param buffer
     * @return
     */
    public static int CRC16_CCITT(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0x8408;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
//        wCRCin=(wCRCin<<8)|(wCRCin>>8);
//        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
 
    }
 
    /**
     * CRC-CCITT (0xFFFF)
           * CRC16_CCITT_FALSE: Polynomial x16+x12+x5+1 (0x1021), initial value 0xFFFF, low bit is after, high bit is first, result is exclusive to 0x0000
     *
     * @param buffer
     * @return
     */
    public static String CRC16_CCITT_FALSE(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x1021;
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return Integer.toHexString(wCRCin ^= 0x0000).toUpperCase();
    }
 
    /**
     * CRC-CCITT (XModem)
           * CRC16_XMODEM: Polynomial x16+x12+x5+1 (0x1021), initial value 0x0000, low bit after, high bit first, result is XOR0
     *
     * @param buffer
     * @return
     */
    public static int CRC16_XMODEM(byte[] buffer) {
        int wCRCin = 0x0000; // initial value 65535
        int wCPoly = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
    }
 
 
    /**
           * CRC16_X25: Polynomial x16+x12+x5+1 (0x1021), initial value 0xffff, low bit first, high bit after, result is XORIF 0xFFFF
           * 0x8408 is the result of 0x1021 bitwise reversal.
     * @param buffer
     * @return
     */
    public static int CRC16_X25(byte[] buffer) {
        int wCRCin = 0xffff;
        int wCPoly = 0x8408;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }
 
    /**
     * CRC-16 (Modbus)
           * CRC16_MODBUS: Polynomial x16+x15+x2+1 (0x8005), initial value 0xFFFF, low bit first, high bit after, result is X2000000 exclusive OR
           * 0xA001 is the result of 0x8005 bitwise reversal
     * @param buffer
     * @return
     */
    public static int CRC16_MODBUS(byte[] buffer) {
        int wCRCin = 0xffff;
        int POLYNOMIAL = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= POLYNOMIAL;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0x0000;
    }
 
    /**
     * CRC-16
           * CRC16_IBM: Polynomial x16+x15+x2+1 (0x8005), initial value 0x0000, low bit first, high bit after, result is XOR0
           * 0xA001 is the result of 0x8005 bitwise reversal
     * @param buffer
     * @return
     */
    public static int CRC16_IBM(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0x0000;
    }
 
    /**
           * CRC16_MAXIM: Polynomial x16+x15+x2+1 (0x8005), initial value 0x0000, low bit first, high bit after, result is XORIF 0xFFFF
           * 0xA001 is the result of 0x8005 bitwise reversal
     * @param buffer
     * @return
     */
    public static int CRC16_MAXIM(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }
 
    /**
           * CRC16_USB: Polynomial x16+x15+x2+1 (0x8005), initial value 0xFFFF, low bit first, high bit after, result is XORIF 0xFFFF
           * 0xA001 is the result of 0x8005 bitwise reversal
     * @param buffer
     * @return
     */
    public static int CRC16_USB(byte[] buffer) {
        int wCRCin = 0xFFFF;
        int wCPoly = 0xa001;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }
 
    /**
           * CRC16_DNP: Polynomial x16+x13+x12+x11+x10+x8+x6+x5+x2+1 (0x3D65), initial value 0x0000, low bit first, high bit after, result is XORIF 0xFFFF
     * 0xA6BC is the result of 0x3D65 bitwise reversal
     * @param buffer
     * @return
     */
    public static int CRC16_DNP(byte[] buffer) {
        int wCRCin = 0x0000;
        int wCPoly = 0xA6BC;
        for (byte b : buffer) {
            wCRCin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wCRCin & 0x0001) != 0) {
                    wCRCin >>= 1;
                    wCRCin ^= wCPoly;
                } else {
                    wCRCin >>= 1;
                }
            }
        }
        return wCRCin ^= 0xffff;
    }
}