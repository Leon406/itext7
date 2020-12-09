package me.leon;

import com.google.common.io.Resources;

import java.nio.charset.Charset;
import java.util.Arrays;

public class Encoding {

    public static final String[] ENCODING_LIST = {"UTF-16", "UTF-8", "ASCII", "ISO-8859-1", "GBK", "UNICODE", "BIG5", "UTF-16BE", "UTF-16LE"};
    private static Charset ISO_8859_1, GBK;

    public static String detectEncoding(String str) {

        for (String encode : ENCODING_LIST) {
            try {
                if (str.equals(new String(str.getBytes(), encode))) {
                    return encode;
                }
            } catch (Exception ex) {
            }
        }

        return "unknown";
    }

    /**
     * 字符串转换unicode
     *
     * @param string
     * @return
     */
    public static String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            unicode.append("\\u" + Integer.toHexString(string.charAt(i)));
        }
        return unicode.toString();
    }

    /**
     * unicode 转字符串
     *
     * @param unicode
     * @return
     */
    public static String unicodeToString(String unicode) {
        StringBuilder string = new StringBuilder();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            string.append((char) Integer.parseInt(hex[i], 16));
        }
        return string.toString();
    }

    public static void main(String[] args) {

        System.out.println(Encoding.class.getClassLoader().getResource("me.leon/66.pdf"));


        //获取系统默认编码
        System.out.println("系统默认编码" + System.getProperty("file.encoding")); //查询结果GBK
        //系统默认字符编码
        System.out.println("系统默认字符编码" + Charset.defaultCharset()); //查询结果GBK
        System.out.println("系统默认语言" + System.getProperty("user.language")); //查询结果zh

        String s1 = "hi, nice to meet you!";
        String s2 = "淘！我喜欢！";
        ISO_8859_1 = Charset.forName("ISO-8859-1");
        GBK = Charset.forName("GBK");
        System.out.println("编码: " + Arrays.toString(s2.getBytes(ISO_8859_1)));
        System.out.println("解码: " + new String(s2.getBytes(ISO_8859_1)));

        System.out.println("编码: " + Arrays.toString(s2.getBytes(GBK)));
        System.out.println("解码: " + new String(s2.getBytes(GBK), GBK));

        System.out.println("编码1: " + Arrays.toString(s2.getBytes(GBK)));
        System.out.println("解码1: " + new String(s2.getBytes(GBK), ISO_8859_1));
        System.out.println("编码2: " + Arrays.toString((new String(s2.getBytes(GBK), ISO_8859_1).getBytes(ISO_8859_1))));
        System.out.println("解码2: " + new String(new String(s2.getBytes(GBK), ISO_8859_1).getBytes(ISO_8859_1),GBK));

        String s3 = new String(s2.getBytes());
        System.out.println(s3);
//        System.out.println(detectEncoding(s1));
//        System.out.println(detectEncoding(s2));
        System.out.println(detectEncoding(s3));
//        System.out.println(stringToUnicode(s2));
//        System.out.println(unicodeToString(stringToUnicode(s2)));
    }
}