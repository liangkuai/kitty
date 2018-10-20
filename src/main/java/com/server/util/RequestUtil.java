package com.server.util;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liangkuai
 * @date 2018/10/21
 */
public class RequestUtil {

    public static Cookie[] parseCooikeHeader(String header) {

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
}
