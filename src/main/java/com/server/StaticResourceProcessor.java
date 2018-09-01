package com.server;

import java.io.IOException;

/**
 * @author liangkuai
 * @date 2018/9/1
 */
public class StaticResourceProcessor {

    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
