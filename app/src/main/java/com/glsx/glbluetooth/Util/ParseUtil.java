/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */

package com.glsx.glbluetooth.Util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：DSS_808 类名称：ParseUtil 类描述：协议解析工具类 创建人：Administrator 创建时间：2016-6-20
 * 下午6:51:43 修改人：Administrator 修改时间：2016-6-20 下午6:51:43 修改备注：
 *
 * @version 1.0
 */
public class ParseUtil {

    private static final String TAG = "ParseUtil";

    /**
     * 仅保留数字与字母
     */
    public static String remainNumAndEng(String str) {
        String ret = str.replaceAll("[^a-z^A-Z^0-9]", "");
        return ret;
    }

    public static String list2HexStringWithoutSpace(List<String> list) {
        String ret = Arrays.toString(list.toArray());
        ret = ret.replaceAll("[^a-z^A-Z^0-9]", "");
        return ret;
    }

    public static final String bytes2HexString(byte[] bArray, int length) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());

            if (i < (length - 1)) {// last byte no need space
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * yyMMDDhhmmss to long
     */
    public static long string2Time(String timeStr) {
        long time = 0l;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyMMddHHmmss");
            Date date = simpleDateFormat.parse(timeStr);
            time = date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    /**
     * time to yyMMDDhhmmss
     */
    public static String formatTime(long t) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String time = sdf.format(new Date(t));
        return time;
    }

    /**
     * 获取指定长度"\0"结尾的数组转字符串
     *
     * @param startIndex
     * @param length
     * @return
     */
    public static byte[] byteToSubstringToByte(byte data[], int startIndex,
                                               int length) {

        try {
            byte[] strbyte = null;
            if (data != null && data.length > 0) {

                String dataHex = parseByte2HexStr(byteTobyte(data, startIndex,
                        length));
                // 用“00”结尾截取十六进制字符串
                String[] strarr = replaceStr(dataHex, "00", "##").split("##");
                // //用“00”结尾截取十六进制字符串
                // String[] strarr = parseByte2HexStr(byteTobyte(data,
                // startIndex, length)).split("00");
                // 得到字符串二进制
                if (strarr != null && strarr.length > 0) {
                    strbyte = parseHexStr2Byte(strarr[0]);
                }
            }
            return strbyte;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转换成指定长度的字符串
     *
     * @param value
     * @param len
     * @return
     */
    public static String stringToString(String value, int len) {
        if (value == null) {
            value = "";
        }
        int length = value.length();
        if (length > len) {
            value = value.substring(0, len);
        } else {
            for (int i = 0; i < (len - length); i++) {
                value = "0" + value;
            }
        }
        return value;

    }

    /**
     * 每两位加空格
     */
    public static String formatWithSpace(String replace) {
        String regex = "(.{2})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace.trim();
    }

    /**
     * 获取指定长度"\0"结尾的数组转字符串
     *
     * @param data       字节数据包
     * @param startIndex 开始位置
     * @param length     截取长度
     * @return 截取后的字节数据包
     */
    public static byte[] byteToSubStringToByte(byte data[], int startIndex,
                                               int length) {

        try {
            String dataHex = ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(
                    data, startIndex, length));
            // 用“00”结尾截取十六进制字符串
            String[] strarr = replaceStr(dataHex, "00", "##").split("##");
            byte[] strbyte = null;
            if (strarr.length == 0) {
                strbyte = new byte[0];
            } else {
                if ("".equals(strarr[0])) {
                    strbyte = new byte[0];
                } else {
                    strbyte = parseHexStr2Byte(strarr[0]);
                }

            }
            return strbyte;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 将字符串转换成二进制
     *
     * @param str
     * @return
     */
    public static byte[] stringToByte(String str) {
        try {
            return (str + "\0").getBytes("GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将整数转换成二进制字节（先低字节后高字节）
     *
     * @param iSource
     * @param iArrayLen
     * @return
     */
    public static byte[] int2Bytes(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    /**
     * 将整数转换成二进制字节（先高字节后低字节）
     *
     * @param iSource
     * @param iArrayLen
     * @return
     */
    public static byte[] int2BytesSmallEnd(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> ((iArrayLen - 1) * 8 - 8 * i) & 0xFF);
        }
        return bLocalArr;
    }

    /**
     * 获取整数转换成二进制字节
     *
     * @return
     */
    public static String int2BytesStr(int iSource, int iArrayLen) {
        //String str = parseByte2HexStr(int2Bytes(iSource, iArrayLen));
        String str = parseByte2HexStr(int2BytesSmallEnd(iSource, iArrayLen));
        return str;
    }

    /**
     * 获取指定长度字节数
     *
     * @param src
     * @param startIndex
     * @param length
     * @return
     */
    public static byte[] byteTobyte(byte[] src, int startIndex, int length) {
        byte[] des = new byte[length];
        int i = 0;
        for (int j = startIndex; i < length; ++j) {
            des[i] = src[j];
            ++i;
        }
        return des;
    }

    public static byte[] longToByteOne(long num) {
        byte[] b = new byte[1];
        b[0] = (byte) (int) (num >>> 0);
        return b;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {

        if (hexStr.length() < 1)

            return null;

        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {

            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);

            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);

            result[i] = (byte) (high * 16 + low);

        }

        return result;
    }

    /**
     * 字符串转换成ascii的16进制
     *
     * @param value
     * @return
     */
    public static String stringToAsciiHexString(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toHexString((int) chars[i]));
        }
        return sbu.toString();
    }


    /**
     * Stirng转成指定长度的ascii的16进制String
     *
     * @param s
     * @param length 指定字节数
     * @return
     */
    public static String stringToAsciiHexStringWithFixationLength(String s, int length) {
        String ascStr = stringToAsciiHexString(s);
        int c = ascStr.length() / 2;
        if (c < length) {
            for (int i = 0; i < (length - c); i++) {
                ascStr += "00";
            }
        }
        return ascStr;
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * 1) 报文的发送端需要将待发送报文的数据内容中（从版本号至校验位，包括版本号和校验位）出现的1002H转义为0x10100202，
     * 将出现的1003H转义为0x10100303。
     *
     * @param str
     * @return
     */
    public static byte[] tropeByte(String str, int type) {
        byte[] result = new byte[str.length() / 2];
        if (type == 1) {
            if (str.indexOf("1002") >= 0) {
                str = str.replaceAll("1002", "10100202");
            }

            if (str.indexOf("1003") >= 0) {
                str = str.replaceAll("1003", "10100203");
            }
        }
        // if(str.indexOf("10")>=0){
        // str = str.replaceAll("10", "1010");
        // }
        str = replaceStr(str, "10", "1010");
        result = ParseUtil.parseHexStr2Byte(str);

        return result;
    }

    /**
     * 为保证数据传输的透明，需对信息字段中出现的标志位进行转义处理，定义如下 7EH 《————》 7DH+02H； 7DH 《————》
     * 7DH+01H；
     *
     * @param str
     * @param type
     * @return
     */
    public static byte[] tropeYXByte(String str, int type) {

        byte[] result = new byte[str.length() / 2];

        if (type == 1) {
            str = replaceStr(str, "7D", "7D01");
            str = replaceStr(str, "7E", "7D02");
            // if(str.indexOf("7D")>=0){
            // str = str.replaceAll("7D", "7D01");
            // }
            //
            // if(str.indexOf("7E")>=0){
            // str = str.replaceAll("7E", "7D02");
            // }
        }
        str = replaceStr(str, "10", "1010");
        result = ParseUtil.parseHexStr2Byte(str);

        return result;
    }

    /**
     * 转义操作
     *
     * @param str
     * @param thq
     * @param thh
     * @return
     */
    public static String replaceStr(String str, String thq, String thh) {

        StringBuffer des = new StringBuffer();
        String s = "";
        str = str + " ";
        for (int i = 0; i < str.length(); i++) {

            if (i % 2 == 0) {
                if (s.equals(thq)) {
                    s = thh;
                }

                des.append(s);
                s = "";
                s = s + str.charAt(i);
            } else {
                s = s + str.charAt(i);
            }
        }
        return des.toString();

    }

    /**
     * 转义操作（2个字符转4个字符）
     *
     * @param str
     * @param thq
     * @param thh
     * @return
     */
    public static String replaceTwoToFour(String str, String thq, String thh) {

        StringBuffer des = new StringBuffer();
        String s = "";
        str = str + " ";
        for (int i = 0; i < str.length(); i++) {

            if (i % 2 == 0) {
                if (s.equals(thq)) {
                    s = thh;
                }

                des.append(s);
                s = "";
                s = s + str.charAt(i);
            } else {
                s = s + str.charAt(i);
            }
        }
        return des.toString();

    }

    /**
     * 4个字符转2个字符
     *
     * @param data
     * @param thq
     * @param thh
     * @return
     */
    public static String replaceFourToTwo(String data, String thq, String thh) {
        StringBuffer dataBuffer = new StringBuffer();
        for (int i = 0; i < data.length(); i += 2) {
            String dStr = data.substring(i, i + 2);
            dataBuffer.append(dStr);
            String desc = dataBuffer.toString();
            if (desc.endsWith(thq)) {
                dataBuffer = new StringBuffer();
                desc = desc.substring(0, desc.length() - 4);
                dataBuffer.append(desc);
                dataBuffer.append("##");
            }
        }
        return dataBuffer.toString().replaceAll("##", thh);
    }

    /**
     * 对DTU协议进行逆转义(1010转10)
     *
     * @param bytes
     * @return
     */
    public static byte[] dtuTransferredMeaning(byte[] bytes) {

        StringBuffer des = new StringBuffer();
        String str = "";
        for (int j = 0; j < bytes.length; j++) {

            String hex = Integer.toHexString(bytes[j] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            if (hex.equals("10")) {
                str = str + hex;
                if (str.indexOf("1010") >= 0) {
                    str = str.replaceAll("1010", "10");
                    des.append(str);
                    str = "";
                }

            } else {
                if (str.indexOf("1010") >= 0) {
                    str = str.replaceAll("1010", "10");
                }
                des.append(str);
                str = "";
                des.append(hex);
            }
        }
        return ParseUtil.parseHexStr2Byte(des.toString());
    }

    /**
     * 计价器与智能顶灯通信协议逆转 2) 报文的接收端在找到报文头（1002H）后，接收数据内容（从版本号至校验位，包括版本号和校验位），
     * 在接收过程中，需要将遇到的0x10100202逆转义为0x1002，将将遇到的0x10100303逆转义为0x1003
     *
     * @param bytes
     * @return
     */
    public static byte[] taximeterToplightReversal(byte[] bytes) {
        String str = parseByte2HexStr(bytes);
        str = str.replaceAll("10100202", "1002").replaceAll("10100303", "1003");
        return parseHexStr2Byte(str);

    }

    /**
     * 转义
     *
     * @param bytes
     * @return
     */
    public static byte[] yxReversal(byte[] bytes) {
        String data = parseByte2HexStr(bytes).toUpperCase();
        if (data.indexOf("7D02") >= 0) {
            data = replaceFourToTwo(data, "7D02", "7E");
        }

        if (data.indexOf("7D01") >= 0) {
            data = replaceFourToTwo(data, "7D01", "7D");
        }
        return parseHexStr2Byte(data);
    }

    /**
     * 9A转义
     *
     * @param bytes
     * @param type
     * @return
     */
    public static byte[] data9AEscape(byte[] bytes, int type) {
        String data = parseByte2HexStr(bytes).toUpperCase();

        if (type == 1) {
            if (data.indexOf("9D02") >= 0) {
                data = replaceFourToTwo(data, "9D02", "9A");
            }

            if (data.indexOf("9D01") >= 0) {
                data = replaceFourToTwo(data, "9D01", "9D");
            }
            return parseHexStr2Byte(data);
        } else {
            byte[] result = new byte[data.length() / 2];
            data = replaceTwoToFour(data, "9D", "9D01");
            data = replaceTwoToFour(data, "9A", "9D02");
            result = parseHexStr2Byte(data);
            return result;
        }
    }

    /**
     * 高低位反向排序
     *
     * @param bytes
     * @return
     */
    public static byte[] sortToByte(byte[] bytes) {
        byte[] des = new byte[bytes.length];
        int i = 0;
        for (int j = bytes.length - 1; j >= 0; j--) {
            des[i] = bytes[j];
            i++;
        }
        return des;
    }

    /**
     * 异或运算和
     *
     * @param bytes
     * @return
     */
    public static byte[] byteOrbyte(byte[] bytes) {
        byte[] orbyte = new byte[1];
        byte value = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            value = (byte) (value ^ bytes[i]);
        }
        orbyte[0] = value;
        return orbyte;
    }

    /**
     * String的字符串转换成unicode的String
     */
    public static String stringToUnicode(String strText) throws Exception {
        char c;
        String strRet = "";
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                strRet += "\\u" + strHex;
            } else {
                // 低位在前面补00
                strRet += "\\u00" + strHex;
            }
        }
        return strRet;
    }

    /**
     * unicode的String转换成String的字符串
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * 得到经纬度的度、分（分+小数点后四位）
     *
     * @param latlng
     * @param type   1 返回度 2 返回分 3 返回小数点后（1至2）两位 4 返回小数点（3至4）两位
     * @return 十六进制字符串
     */
    public static String getLatLng(double latlng, int type) {

        try {
            int reLatlng = 0;
            double min = (latlng - (int) latlng) * 60;
            String minStr = String.valueOf(min).substring(
                    String.valueOf(min).indexOf(".") + 1,
                    String.valueOf(min).length());
            int size = minStr.length();
            if (minStr.length() < 4) {
                for (int i = 0; i < (4 - size); i++) {
                    minStr += "0";
                }
            }
            switch (type) {
                case 1:
                    reLatlng = (int) latlng;
                    break;
                case 2:
                    reLatlng = (int) ((latlng - (int) latlng) * 60);
                    break;
                case 3:
                    reLatlng = Integer.parseInt(minStr.substring(0, 2));
                    break;
                case 4:
                    reLatlng = Integer.parseInt(minStr.substring(2, 4));
                    break;
                default:
                    break;
            }
            return ParseUtil.parseByte2HexStr(ParseUtil
                    .longToByteOne((reLatlng)));
        } catch (Exception e) {
            LLog.e(TAG, e.getMessage());
            e.printStackTrace();
            return "00";
        }
    }

    /**
     * 解码经纬度
     *
     * @param latlngbyte
     * @return
     */
    public static Double decoderLatlng(byte[] latlngbyte) {
        try {
            int no = 0;
            byte[] ad = ParseUtil.byteTobyte(latlngbyte, no, 1);
            int adi = Integer.parseInt(ParseUtil.parseByte2HexStr(ad), 16);
            no += 1;
            byte[] ac = ParseUtil.byteTobyte(latlngbyte, no, 1);
            no += 1;
            byte[] ac1 = ParseUtil.byteTobyte(latlngbyte, no, 1);
            no += 1;
            byte[] ac2 = ParseUtil.byteTobyte(latlngbyte, no, 1);
            no += 1;
            StringBuffer acBuffer = new StringBuffer();
            acBuffer.append(Integer.parseInt(ParseUtil.parseByte2HexStr(ac), 16));
            acBuffer.append(Integer.parseInt(ParseUtil.parseByte2HexStr(ac1),
                    16));
            acBuffer.append(Integer.parseInt(ParseUtil.parseByte2HexStr(ac2),
                    16));
            Double acDouble = Double.parseDouble(acBuffer.toString()) / 10000;
            Double latlng = adi + acDouble / 60;
            DecimalFormat df = new DecimalFormat("#.000000");
            return Double.parseDouble(df.format(latlng));
        } catch (Exception e) {
            e.printStackTrace();
            LLog.e(TAG, e.getMessage());
            return 0.000000;
        }
    }

    /**
     * 得到16进制 年 月 日 时 分 秒
     *
     * @param formart
     * @return 十六进制字符串
     */
    public static String getDateTime(String formart) {
        String str = "00";
        try {
            String systemdate = new SimpleDateFormat(formart).format(Calendar
                    .getInstance().getTime());
            str = parseByte2HexStr(longToByteOne(Long.parseLong(systemdate)));
        } catch (Exception e) {
            LLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return str;

    }

    /**
     * 将二进制转换成字符串
     *
     * @param src
     * @param startIndex
     * @param length
     * @return
     */
    public static String byteToString(byte[] src, int startIndex, int length) {
        byte[] des = new byte[length];
        int i = 0;
        for (int j = startIndex; i < length; ++j) {
            des[i] = src[j];
            ++i;
        }
        String str = null;
        try {
            str = new String(des, "gb2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            LLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return str.trim();
    }

    /**
     * 数字字符串转ASCII码字符串
     *
     * @param content 转换内容
     * @return 转换后的ASCII码字符串
     */
    public static String stringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }

    /**
     * 字节转换成位
     *
     * @param bytes
     * @return
     */
    public static String byteTobit(byte[] bytes) {
        String str = "";
        for (int j = 0; j < bytes.length; j++) {
            for (int i = 7; i >= 0; --i) {
                str += (bytes[j] & (1 << i)) == 0 ? '0' : '1';
            }
        }
        return str;
    }

    /**
     * BCD码转为10进制串(阿拉伯数据)
     *
     * @param bytes 字节数组
     * @return 转换后的字符串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * 10进制串转为BCD码
     *
     * @param asc 字符串
     * @return BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static byte[] str2Bcd(String asc, int length) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        if (len / 2 < length) {
            for (int i = 0; i < length - len / 2; i++) {
                asc += "00";
            }
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 转换成指定长度的字符串 少的部分以空格填充
     *
     * @param s
     * @param fieldLength
     * @return
     */
    public static String appendSpaceRight(String s, int fieldLength) {
        if (s == null) {
            s = "";
        }
        String ret = s;
        int stringLength = s.length();
        if (stringLength < fieldLength) {
            for (int i = stringLength; i < fieldLength; ++i) {
                ret = ret + " ";
            }
        } else if (stringLength > fieldLength) {
            ret = ret.substring(0, fieldLength);
        }
        return ret;
    }

    /**
     * 转换成指定长度的字符串 少的部分以0填充
     *
     * @param s
     * @param fieldLength
     * @return
     */
    public static String appendRight(String s, int fieldLength) {
        if (s == null) {
            s = "";
        }
        String ret = s;
        int stringLength = s.getBytes().length;
        if (stringLength < fieldLength) {
            for (int i = stringLength; i < fieldLength; ++i) {
                ret = ret + "0";
            }
        } else if (stringLength > fieldLength) {
            ret = ret.substring(0, fieldLength);
        }
        return ret;
    }

    /**
     * 返回指定长度的字节数组
     *
     * @param str
     * @param len
     * @return
     */
    public static byte[] getByteToByte(String str, int len) {
        byte[] body = new byte[len];
        int dstPos = 0;
        // 省域ID
        System.arraycopy(str.getBytes(), 0, body, dstPos, str.getBytes().length);
        return body;
    }

    // 将byte数组转换成InputStream
    public static InputStream byteTOInputStream(byte[] in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in);

        return is;

    }

    /**
     * 字符串转换成十六进制字符串
     */

    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder("");

        byte[] bs = str.getBytes();

        int bit;

        for (int i = 0; i < bs.length; i++) {

            bit = (bs[i] & 0x0f0) >> 4;

            sb.append(chars[bit]);

            bit = bs[i] & 0x0f;

            sb.append(chars[bit]);

        }

        return sb.toString();

    }

    /**
     * 获取
     *
     * @param str
     * @param length
     * @return
     */
    public static String str2HexStr(String str, int length) {

        return null;
    }

    /**
     * 十六进制转换字符串
     */

    public static String hexStr2Str(String hexStr) {

        String str = "0123456789ABCDEF";

        char[] hexs = hexStr.toCharArray();

        byte[] bytes = new byte[hexStr.length() / 2];

        int n;

        for (int i = 0; i < bytes.length; i++) {

            n = str.indexOf(hexs[2 * i]) * 16;

            n += str.indexOf(hexs[2 * i + 1]);

            bytes[i] = (byte) (n & 0xff);

        }

        return new String(bytes);

    }

    public static char ascii2Char(int ASCII) {
        return (char) ASCII;
    }

    public static int char2ASCII(char c) {
        return (int) c;
    }

    /**
     * 将 ascii转换成字符串
     *
     * @param ASCIIs
     * @return
     */
    public static String ascii2String(String ASCIIs) {
        String[] ASCIIss = ASCIIs.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ASCIIss.length; i++) {
            sb.append((char) ascii2Char(Integer.parseInt(ASCIIss[i])));
        }
        return sb.toString();
    }

    /**
     * byte——>String
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 域名转换成IP
     *
     * @param domain
     * @return
     */
    public static String domainToip(String domain) {
        String ip = "";
        try {
            InetAddress inetHost = InetAddress.getByName(domain);
            ip = inetHost.getHostAddress();
            System.out.println("ip=" + ip);
        } catch (UnknownHostException e) {
            LLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return ip;
    }

    public static boolean isIp(String IP) {// 判断是否是一个IP
        boolean b = false;
        IP = clearSpace(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String s[] = IP.split("\\.");
            if (Integer.parseInt(s[0]) < 255)
                if (Integer.parseInt(s[1]) < 255)
                    if (Integer.parseInt(s[2]) < 255)
                        if (Integer.parseInt(s[3]) < 255)
                            b = true;
        }
        return b;
    }

    public static String clearSpace(String IP) {// 去掉IP字符串前后所有的空格
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param newCharset 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String newCharset)
            throws UnsupportedEncodingException {
        if (str != null) {
            // 用默认字符编码解码字符串。
            byte[] bs = str.getBytes();
            // 用新的字符编码生成字符串
            return new String(bs, newCharset);
        }
        return null;
    }

    /**
     * 校验和
     *
     * @param msg    需要计算校验和的byte数组
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    public static byte[] sumCheck(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg
                    : ((long) byteMsg + 256);
            mSum += mNum;
        }
        /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        }

        return mByte;
    }

    /**
     * 从字符串中获取数字
     */
    public static String filtrateNumber(String text) {
        if (text != null && !text.equals("")) {
            String number = "";
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(text);
            number = m.replaceAll("").trim();
            return number;
        } else {
            return null;
        }
    }

    /**
     * 带小数点字符串转整数
     *
     * @param str
     * @param num
     * @return
     */
    public static int stringToint(String str, int num) {
        try {
            return (int) (Double.parseDouble(str) * num);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 读取application 节点 meta-data 信息
     *
     * @param mContext
     * @param key
     * @return
     */
    public static String readMetaDataFromApplication(Context mContext,
                                                     String key) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 保存文件到sd卡
     *
     * @param bytes 二进制数组
     * @param _path 保存路径
     * @return true 成功 false 失败
     */
    public static boolean saveToFile(byte[] bytes, String _path) {

        try {
            BufferedOutputStream os = null;
            File file = new File(_path, "TCIOV_Nebula.apk");
            int end = _path.lastIndexOf(File.separator);
            String _filePath = _path.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(bytes);
            if (os != null) {
                os.close();
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 获取SD卡路径
     *
     * @return path
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }

        return sdDir.toString();
    }

    /**
     * install apk
     *
     * @param context
     * @param filePath
     * @return 0 means normal, 1 means file not exist, 2 means other exception
     * error
     */
    public static int installSlient(Context context, String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0
                || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {
            return 1;
        }

        String[] args = {"pm", "install", "-r", filePath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = 2;
        } catch (Exception e) {
            e.printStackTrace();
            result = 2;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success")
                || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = 2;
        }
        Log.d("installSlient", "successMsg:" + successMsg + ", ErrorMsg:"
                + errorMsg);
        return result;
    }

    /**
     * 获取代码版本号
     *
     * @param context
     * @return
     */
    public static int getCodeVersion(Context context) {
        int codeVersion = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            codeVersion = info.versionCode; // 版本名
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return codeVersion;

    }

    public static void main(String[] args) {

    }

    public static byte[] readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }

    public static boolean copyFile(String o, String n) {
        try {
            File oldFile = new File(o);
            if (!oldFile.exists()) {
                LLog.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                LLog.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                LLog.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(o);
            FileOutputStream fileOutputStream = new FileOutputStream(n);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> int2HexStrList(int iSource, int iArrayLen) {
        byte[] datas = int2BytesSmallEnd(iSource, iArrayLen);
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {
            strList.add(byte2HexStr(datas[i]));
        }
        return strList;
    }

    public static String byte2HexStr(byte value) {
        String strRet = Integer.toHexString(value & 0xFF);
        if (strRet.length() < 2) {
            strRet = "0" + strRet;
        }
        return strRet.toUpperCase();
    }

    /**
     * 将16进制的数字字符串转换为对应的byte值
     *
     * @param hexStr
     * @return
     */
    public static byte hexStr2Byte(String hexStr) {
        int intValue = Integer.parseInt(hexStr, 16);
        byte value = (byte) (intValue & 0xFF);
        return value;
    }

    /**
     * 将16进制数字字符串列表转换为byte[]数组
     *
     * @param hexStrList
     * @return
     */
    public static byte[] hexStrList2Bytes(List<String> hexStrList) {
        if (hexStrList == null || hexStrList.size() == 0) {
            return null;
        }
        byte[] datas = new byte[hexStrList.size()];
        for (int i = 0; i < hexStrList.size(); i++) {
            datas[i] = hexStr2Byte(hexStrList.get(i));
        }
        return datas;
    }

    /**
     * 将16进制的数字字符串列表转换为charsetName对应的字符串
     *
     * @param hexStrList
     * @param charsetName
     * @return
     */
    public static String hexStrList2Str(List<String> hexStrList, String charsetName) {
        byte[] datas = hexStrList2Bytes(hexStrList);
        String result = null;
        try {
            result = new String(datas, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将16进制的数字字符串列表通过ASCCII格式转码转换为对应的字符串
     *
     * @param hexStrList
     * @return
     */
    public static String hexStrList2AsciiStr(List<String> hexStrList) {
        return hexStrList2Str(hexStrList, "US-ASCII");
    }

    /**
     * 将16进制的数字字符串列表通过GBK格式转码转换为对应的字符串
     *
     * @param hexStrList
     * @return
     */
    public static String hexStrList2GbkStr(List<String> hexStrList) {
        return hexStrList2Str(hexStrList, "GBK");
    }

    public static List<String> formatCardInfo(List<String> data) {
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            if ("00".equals(data.get(i))) {
                index = i;
                break;
            }
        }
        if (index != 0) {
            return data.subList(0, index);
        }
        return data;
    }

    /**
     * 将时间格式字符串转换为时间戳
     *
     * @param str     时间字符串
     * @param pattern 时间字符串
     * @return 时间戳
     */
    public static long strToTimeStamp(String str, String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            return 0;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        ParsePosition pos = new ParsePosition(0);
        Date data = formatter.parse(str, pos);
        return data.getTime();
    }

    /**
     * 将时间戳转换为时间串
     *
     * @param lt   时间戳
     * @param type pattern类型
     * @return 格式化时间串
     */
    public static String timeStampToStr(long lt, int type) {
        String format = "yyyy-MM-dd HH:mm:ss";
        if (type == 1) {
            format = "yyyy-MM-dd HH:mm";
        } else if (type == 2) {
            format = "yyyy-MM-dd";
        } else if (type == 3) {
            format = "yyyy-MM";
        } else if (type == 4) {
            format = "MM-dd HH:mm";
        } else if (type == 5) {
            format = "MM-dd";
        } else if (type == 6) {
            format = "HH:mm";
        } else if (type == 7) {
            format = "dd";
        } else if (type == 8) {
            format = "yy/MM/dd";
        } else if (type == 9) {
            format = "mm:ss";
        } else if (type == 10) {
            format = "HH:mm:ss";
        } else if (type == 11) {
            format = "yyyyMMddHHmmss";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

}
