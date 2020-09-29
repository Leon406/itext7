package me.leon;

import java.nio.charset.Charset;

public class Encoding {
    public static String getEncoding(String str) {
        String encode;

        encode = "UTF-16";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ex) {
        }

        encode = "ASCII";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return "字符" + str + " >>中仅由数字和英文字母组成，无法识别其编码格式";
            }
        } catch (Exception ex) {
        }

        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ex) {
        }

        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ex) {
        }

        encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ex) {
        }

        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ex) {
        }

        return "unknown";
    }
    /**
     * 字符串转换unicode
     * @param string
     * @return
     */
    public static String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            unicode.append("\\u" +Integer.toHexString(string.charAt(i)));
        }
        return unicode.toString();
    }

    /**
     * unicode 转字符串
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
        //获取系统默认编码
        System.out.println("系统默认编码" + System.getProperty("file.encoding")); //查询结果GBK
        //系统默认字符编码
        System.out.println("系统默认字符编码" + Charset.defaultCharset()); //查询结果GBK
        System.out.println("系统默认语言" + System.getProperty("user.language")); //查询结果zh

        String s1 = "hi, nice to meet you!";
        String s2 = "hi, 我来了！";
        System.out.println(getEncoding(s1));
        System.out.println(getEncoding(s2));
        System.out.println(stringToUnicode(s2));
        System.out.println(unicodeToString(stringToUnicode(s2)));
    }
}