package com.server.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * connector
 *
 * @author liangkuai
 * @date 2018/9/9
 */
public class HttpConnector implements Runnable {

    boolean stopped;
    private String scheme = "http";

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stopped) {
            // 接受连接
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                continue;
            }

            // 将套接字交给 HttpProcessor
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket);
        }
    }


    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }


    public String getScheme() {
        return this.scheme;
    }
}
