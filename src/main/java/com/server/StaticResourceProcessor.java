package com.server;

import com.server.connector.http.HttpRequest;
import com.server.connector.http.HttpResponse;

import java.io.IOException;

/**
 * @author liangkuai
 * @date 2018/9/1
 */
public class StaticResourceProcessor {

    public void process(HttpRequest request, HttpResponse response) {
        try {
            response.sendStaticResource();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
