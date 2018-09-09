package com.server.startup;

import com.server.connector.http.HttpConnector;

/**
 * @author liangkuai
 * @date 2018/9/9
 */
public final class Bootstrap {

    public static void main(String[] args) {

        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
