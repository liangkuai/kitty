package org.look.kitty.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liangkuai
 * @date 2018/10/21
 */
public class RequestUtil {

    /**
     * 解析 cookie 首部
     *
     * @param header cookie 首部值
     */
    public static Cookie[] parseCookieHeader(String header) {

        if (header == null || header.isEmpty()) {
            return new Cookie[0];
        }

        List<Cookie> cookies = new ArrayList<>();
        while (!header.isEmpty()) {
            int semicolon = header.indexOf(";");

            // header value 中仅有一个 cookie，没有分号
            if (semicolon < 0) {
                semicolon = header.length();
            }

            // header value 只有一个分号，显然是个错误
            if (semicolon == 0) {
                break;
            }

            // header 中的一个 cookie
            String token = header.substring(0, semicolon);

            int equals = token.indexOf("=");
            if (equals > 0) {
                String name = token.substring(0, equals).trim();
                String value = token.substring(equals + 1).trim();
                cookies.add(new Cookie(name, value));
            }

            // header 中的剩余部分
            if (semicolon < header.length()) {
                header = header.substring(semicolon + 1);
            } else {
                header = "";
            }
        }

        return cookies.toArray(new Cookie[0]);
    }


    public static void parseParameters(Map map, String data, String encoding) throws UnsupportedEncodingException {
        if ((data != null) && !data.isEmpty()) {
            parseParameters(map, data.getBytes(), encoding);
        }
    }

    public static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException {
        if (data != null && data.length > 0) {

            String key = null;
            String value;
            int rest = 0;
            int i;
            for (i = 0; i < data.length; i++) {

                switch ((char) data[i]) {
                    case '=':
                        key = new String(data, rest, i, encoding);
                        rest = i + 1;
                        break;
                    case '&':
                        value = new String(data, rest, i, encoding);
                        rest = i + 1;
                        if (key != null) {
                            putMapEntity(map, key, value);
                            key = null;
                        }
                        break;
                    case '+':
                        data[i] = ' ';
                        break;
                    case '%':
                        data[i] = (byte)((convertHexDigit(data[i++]) << 4)
                                + convertHexDigit(data[i++]));
                        break;
                    default:
                        break;
                }
            }

            if (key != null) {
                value = new String(data, rest, i, encoding);
                putMapEntity(map, key, value);
            }

        }
    }


    private static void putMapEntity(Map map, String key, String value) {

    }


    private static byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }
}
