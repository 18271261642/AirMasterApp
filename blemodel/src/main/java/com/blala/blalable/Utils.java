package com.blala.blalable;


import android.content.Context;
import android.util.Log;

import org.apache.commons.lang.StringUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by Admin
 * Date 2021/9/16
 */
public class Utils {




    private static final String HEX_CHARS = "0123456789ABCDEF";

    public static int indexPosition = 0;

    public static void setIndexPosition(int indexPosition) {
        Utils.indexPosition = indexPosition;
    }

    public static int getIndexPosition() {
        return indexPosition;
    }

    //一个byte表示
    public static String file1 = "flashcloudpic";
    //两个字节表示
    public  static String file2 = "flashcloudaddr";
    //四个字节表示
    public  static String file3 = "flashcloudwid";
    //字节转换字节
    public static String file4 = "flashcloudcoo";


    public static int byte2Int(byte bt) {
        return bt & 0x000000ff;
    }

    public static int byteToInt(byte data) {
        return (data & 0x00ff);
    }


    /**
     * 获取系统语言是否是中文
     * @param context
     * @return
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    private static StringBuffer stringBuffer = new StringBuffer();
    public static String changeStr(String str){
        stringBuffer.delete(0,stringBuffer.length());
        if(str.length() == 4){
            byte[] arr = hexStringToByte(str);
            stringBuffer.append(String.format("%02x",arr[1]));
            stringBuffer.append(String.format("%02x",arr[0]));
            return stringBuffer.toString();
        }
        return null;
    }

    public static String formatBtArrayToString(byte[] bt){
        StringBuilder stringBuilder = new StringBuilder();
        for(Byte b : bt){
            stringBuilder.append(String.format("%02x",b));
        }
        return stringBuilder.toString();
    }

    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }


    private static byte toByte(char c) {
        return (byte) HEX_CHARS.indexOf(c);
    }
    /**
     * 把16进制字符串转换成字节数组 *     @param hex * @return
     */
    public static byte[] hexStringToByte(String hex) {
        if (hex == null ){
            return null;
        }
        if(hex.length()%2!=0)
            hex="0"+hex;
        hex = hex.trim();
        hex =  hex.toUpperCase();
        int len = (hex.length() / 2);
        char[] hexChars = hex.toCharArray();
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * c无符号的值，转换成java-int值
     */
    public static int cbyte2Int(byte byteNum) {
        return byteNum & 0xff;
    }

    public static String byteToHex(byte b){
        return String.format("%02x",b).toUpperCase();
    }

    /**
     * 十六进制打印数组
     */
    public static String getHexString(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        if(buffer == null || buffer.length == 0)
            return sb.toString();
        for (byte b : buffer) {
            String s = byteToHex(b);
            sb.append(s);
        }
        return sb.toString();
    }


    public static String getHexString(byte buffer) {
        StringBuilder sb = new StringBuilder();
        sb.append(byteToHex(buffer));
        return sb.toString();
    }
    /**
     * 数组长度值为8，每个值代表bit，即8个bit。bit7 -> bit0
     * 	bit数组，bit3 -> bit0
     */
    public static byte[] byteToBitOfArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }


    /**
     * 4个byte转int，不够补0
     * @param high_h
     * @param high_l
     * @param low_h
     * @param low_l
     * @return
     */
    public static   int getIntFromBytes(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }

    public static   int getIntFromBytes(byte low_h, byte low_l) {
        return (low_h & 0xff) << 8 | low_l & 0xff;
    }


    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 0) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 7) & 0x1);
    }


    /**
     * Bit转Byte
     */
    public static byte bitToByte(String byteStr) {
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
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }



    public static String[] faceNameArray = new String[]{file1,file2,file3,file4};


    public static byte[] intToByteArray(int value){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xff);
        bytes[1] = (byte) (value >> 8);
        bytes[2] = (byte) (value >> 16);
        bytes[3] = (byte) (value >> 24);
        return bytes;

    }

    public static byte[] intToSecondByteArray(int value){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value& 0xff);
        bytes[1] = (byte) (value >> 8);
        return bytes;

    }

    /**
     * 高位在前
     * @param value
     * @return
     */
    public static byte[] intToSecondByteArrayHeight(int value){
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (value& 0xff);
        bytes[0] = (byte) (value >> 8);
        return bytes;

    }


    //表盘第二个文件转数组
    public static byte[] listStrToForthByt(List<String> list){
        byte[] resultByte = new byte[list.size()*4];
        for(int i = 0;i<list.size();i++){
            String str = list.get(i);
            int int1 = Integer.parseInt(str.trim());

            byte[] tmpByte = intToByteArray(int1);
            System.arraycopy(tmpByte,0,resultByte,i*4,4);
        }
        return resultByte;
    }


    //表盘第三个文件转byte数组
    public static byte[] onStrToByte(List<String> list){
        byte[] btArrays = new byte[list.size()];
        for(int i = 0;i<list.size();i++){
            String str = list.get(i);
            int it1 = Integer.parseInt(str.trim());
            btArrays[i] = (byte) it1;
        }
        return btArrays;
    }


    //表盘第一个文件转数组
    public static byte[] listStrToByte(List<String> list){
        byte[] btArrays = new byte[list.size()];
        for(int i = 0;i<list.size();i++){
            String tmpStr = list.get(i);
            byte bt;
            if(tmpStr.contains("x")){
                String hexStr = StringUtils.substringAfter(tmpStr,"x");
                 bt = (byte) Integer.parseInt(hexStr.trim(),16);
            }else{
                bt = 0;
            }
            btArrays[i] = bt;
        }
        return btArrays;
    }


    //表盘第四个文件转数组 两个byte
    public static byte[] fourthFaceToArrays(List<String> list){
        byte[] forthByte = new byte[list.size()*2];

        for(int i = 0;i<list.size();i++){
            String currV = list.get(i);
            int tmpCurrV = Integer.parseInt(currV.trim());
            byte[] tmpByte = intToSecondByteArray(tmpCurrV);
            System.arraycopy(tmpByte,0,forthByte,i*2,2);
        }
        return forthByte;

    }


    //处理表盘数据，将总的byte包转换为每64个byte集合
    public static ArrayList<byte[]> dialByteList(byte[] arrays){
        ArrayList<byte[]> listBytes = new ArrayList<>();

        int countIndex = arrays.length/ 64;
        int countRemainder = arrays.length % 64;

        for(int i = 0;i<countIndex;i++){
            byte[] faceBytes = new byte[64];
            System.arraycopy(arrays,i*64,faceBytes,0,64);
            listBytes.add(faceBytes);
        }
      //  Log.e(TAG,"--11--表盘文件有多少包="+listBytes.size());
        if(countRemainder>0){
            byte[] faceBytes = new byte[64];
            System.arraycopy(arrays,countIndex*64,faceBytes,0,countRemainder);
            listBytes.add(faceBytes);
        }
        return listBytes;
    }
    static ArrayList<byte[]> list = new ArrayList<>();


    final static StringBuilder msgSb = new StringBuilder();

    public static ArrayList<byte[]> sendMessageData(int messageType, String title, String content) {
        list.clear();
        msgSb.delete(0,msgSb.length());

      /*  if (true) {
            list.add(new byte[]{0x01, 0x10, 0x01, 0x00, (byte) 0x0B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x02, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x04, 0x10, 0x54, 0x65, 0x73, 0x74, 0x20, 0x6E, 0x6F, 0x74, 0x69, 0x66, 0x79, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            list.add(new byte[]{0x03, 0x10, (byte) 0xE5, 0x07, 0x04, 0x14, 0x54, 0x16, 0x02, 0x19, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

            return list;
        }*/

        byte[] titleByte = null;
        byte[] srccontentByte = null;
        byte[] descontentByte = null;
        byte[] contentlen = null;
        byte[] timeByte = null;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String strMonth = formatTwoStr(calendar.get(Calendar.MONTH) + 1);
        String strDay = formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        String strHour = formatTwoStr(calendar.get(Calendar.HOUR_OF_DAY));
        String strMin = formatTwoStr(calendar.get(Calendar.MINUTE));
        String strSecond = formatTwoStr(calendar.get(Calendar.SECOND));
        //String time = "20200110T1036";
        String time = year + strMonth + strDay + "T" + strHour + strMin + strSecond;
        int conntentLen = 0;
        if (title != null) {
            try {
                titleByte = title.getBytes(StandardCharsets.UTF_8);
                srccontentByte = content.getBytes(StandardCharsets.UTF_8);

                Log.e("内容直接",content+"内="+formatBtArrayToString(srccontentByte));


                for (byte byteChar : srccontentByte) {
                    msgSb.append(String.format("%02X ", byteChar));
                }
                byte[] cBarr = hexStringToByte(msgSb.toString());
                conntentLen = srccontentByte.length;
                Log.e("消息内容" , title + ",srccontentByte:" + msgSb.toString() + "conntentLen:" + conntentLen+" "+formatBtArrayToString(cBarr));
                if (conntentLen > 91) {
                    conntentLen = 91;
                }
                descontentByte = new byte[conntentLen + 1];
                for (int i = 0; i < conntentLen; i++) {
                    descontentByte[i] = srccontentByte[i];
                }
                descontentByte[conntentLen] = 0;

                String contentByteLen = String.valueOf(conntentLen);
                contentlen = contentByteLen.getBytes(StandardCharsets.UTF_8);
                timeByte = time.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        list.add(sendNotiCmd(contentlen, 1, messageType));
        list.add(sendNotiCmd(titleByte, 2));
        int num1 = (conntentLen + 1) / 18;
        int num2 = (conntentLen + 1) % 18;
        int sumNum = 0;
        if (num2 != 0) {
            sumNum = num1 + 1;
        } else {
            sumNum = num1;
        }
        /**
         * src：源数组

         srcPos：源数组要复制的起始位置

         dest：目标数组

         destPos：目标数组复制的起始位置

         length：复制的长度
         */
        list.add(sendNotiCmd(timeByte, 3));
        try {
            for (int i = 0; i < sumNum; i++) {
                byte[] notiContent = new byte[18];
                if (i * 18 + 18 < descontentByte.length) {
                    System.arraycopy(descontentByte, i * 18, notiContent, 0, 18);
                } else {
                    System.arraycopy(descontentByte, i * 18, notiContent, 0, (descontentByte.length - i * 18));
                }
                list.add(sendNotiCmd(notiContent, i + 4));
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

//            list.add(sendNotiCmd(timeByte, 3));
            return list;
        }

        //时间包格式：20200108T105632


    }

    public static byte[] sendNotiCmd(byte[] notiContent, int packageIndex, int notitype) {
        byte[] btCmd = new byte[20];
        for (int i = 0; i < 20; i++) {
            btCmd[i] = 0x00;
        }
        btCmd[0] = (byte) packageIndex;
        btCmd[1] = (byte) 0x10;
        btCmd[2] = (byte) notitype;
        btCmd[3] = (byte) 0x00;
        if (notiContent != null && notiContent.length <= 16) {
            System.arraycopy(notiContent, 0, btCmd, 4, notiContent.length);
        }
        return btCmd;
    }

    public static byte[] sendNotiCmd(byte[] notiContent, int packageIndex) {
        byte[] btCmd = new byte[20];
        try {
            btCmd[0] = (byte) packageIndex;
            btCmd[1] = (byte) 0x10;
            if (notiContent != null && notiContent.length <= 18) {
                System.arraycopy(notiContent, 0, btCmd, 2, notiContent.length);
            } else {
                System.arraycopy(notiContent, 0, btCmd, 2, 18);
            }
            int length = (notiContent == null ? 0 : notiContent.length);
            if (length < 18 && length >= 0) {
                for (int i = length; i < 18; i++) {
                    btCmd[2 + i] = (byte) 0x00;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return btCmd;
        }

    }

    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }


    // 这里替换你的产品id
    private static String product_id = "88";

    /**
     * 获取全包内容
     *
     * @param value 你的 payload
     * @return payload 加上head
     */
    public static byte[] getFullPackage(byte[] value) {
        StringBuilder stringBuilder = new StringBuilder();
        //产品id
        stringBuilder.append(product_id)
                .append("0000")  //Reserve
                .append(getHexString(toByteArray(value.length)))  //长度 ，4个字节
                .append(getHexString(new byte[]{byteXOR(value)}))  //xor crc
                .append(getHexString(value));
        return hexStringToByte(stringBuilder.toString());
    }

    public static byte[] toByteArray(int i) {
        byte[] array = new byte[4];

        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);

        return array;
    }

    /**
     * @param data 要异或的数据
     * @return 最终返回的异或数据结果
     */
    public static byte byteXOR(byte[] data) {
        byte byteXor = 0x00;
        for (int i = 0; i < data.length; i++) {
            byteXor = (byte) (byteXor ^ data[i]);

        }

        return (byte) (byteXor & 0XFF);
    }


    public static String getUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append(String.format("\\u%04x", Integer.valueOf(c)));
        }

        return unicode.toString();
    }


    /**
     * 将string类型的byte数组转成bytep[]
     *
     * @param str
     * @return
     */
    public static byte[] stringToByte(String str) {
        byte[] data = new byte[str.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str.substring(0 + i * 2, 2 + i * 2), 16).byteValue();
        }
        return data;
    }

    /**
     * 这个判断是用来判断是否为需要解析的cmd
     * 需要按照协议，发送的key  和返回的key 保证一一对应
     *
     * @param command 返回的 cmd
     * @return 对应返回 ture , false 不对应
     */

    public static byte[] getPlayer(String command,String key,byte[] value) {  //
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(command)  //command
                .append(key)  //key
                // .append(ByteUtil.getHexString(HexDump.toByteArray((short) value.length)))  //value的长度  最新通知没了不需要了
                .append(getHexString(value));
        return hexStringToByte(stringBuilder.toString());
    }
    public static byte[] getPlayer(String Command,String key ) {  //
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Command)  //command
                .append(key);  //key
        return hexStringToByte(stringBuilder.toString());
    }

    public static byte[] byteMerger(byte[] bt1, byte bt2) {
        byte[] bt3 = new byte[bt1.length + 1];
        int i = 0;
        for (byte bt : bt1) {
            bt3[i] = bt;
            i++;
        }
        bt3[i] = bt2;
        return bt3;
    }

    public static byte[] byteMerger(byte bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt2.length + 1];
        int i = 0;
        bt3[i] = bt1;
        i++;
        for (byte bt : bt2) {
            bt3[i] = bt;
            i++;
        }

        return bt3;
    }

    public static byte[] toByteArrayLength(int i, int size) {
        byte[] array = new byte[size];
        for (int j = 0; j < size; j++) {
            array[j] = (byte) ((i >> 8 * (size - j - 1)) & 0xFF);
        }
        return array;
    }

    /**
     * 数组合并
     * @param first 第一个
     * @param rest 后续的
     * @param <T>类型
     * @return一个数组
     */
    public static <T> byte[] concatAll(byte[] first, byte[]... rest) {

        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }

        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


    public static <T> byte[] concat(byte[] first, byte[] second) {

        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }



    public static byte[] copyArray(byte[] first,byte[] second){
        byte[] result = new byte[first.length+second.length];
        System.arraycopy(first,0,result,0,first.length);
        System.arraycopy(second,0,result,first.length,second.length);
        return result;
    }


    /**
     * 车内容累校验
     */
    public static byte crcCarContent(int sum){
        int v = ((sum ^ 0xff)+1) & 0xff;
        String str = String.format("%02x",v);
        byte[] array = hexStringToByte(str);
        return array[0];
    }


    public static String crcCarContentByteArray(byte[] hexArray){
        int sum = 0;
        for(int i = 0;i<hexArray.length;i++){
            sum+=hexArray[i] & 0xff;
        }
        int v = ((sum ^ 0xff)+1) & 0xff;
        String str = String.format("%02x",v);
        return str;
    }

    public static String crcCarContentArray(String hexArray){
        byte[] array = hexStringToByte(hexArray);
        int sum = 0;
        for(int i = 0;i<array.length;i++){
            sum+=array[i] & 0xff;
        }
        int v = ((sum ^ 0xff)+1) & 0xff;
        String str = String.format("%02x",v);
        return str;
    }


    /**
     * 将字符串转换成ACI->byte[]
     */
    public static byte[] changeStrToAscii(String str){
        byte[] byteArray = new byte[16];
        for(int i = 0;i<str.length();i++){
            char c = str.charAt(i);
            int asciiValue = (int) c; // 将字符转换为对应的ASCII值
            byteArray[i] = (byte) asciiValue;
        }
        return byteArray;
    }



    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    public static String formatCurrentTime(){
        return sdf.format(new Date(System.currentTimeMillis()));
    }


}
