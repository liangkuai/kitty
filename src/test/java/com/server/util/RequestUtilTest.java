package com.server.util;

import org.junit.Test;

/**
 * @author liangkuai
 * @date 2018/10/21
 */
public class RequestUtilTest {

    @Test
    public void parseCooikeHeaderTest() {
//        String header = "jsessionid=123;sessionid=321";
        String header = "jsessionid=123;";
        int index = header.indexOf(";");
        System.out.println(header.charAt(index));
        System.out.println(header.substring(0, index));
        System.out.println(header.substring(index + 1));
        System.out.println(header.substring(index));
    }
}