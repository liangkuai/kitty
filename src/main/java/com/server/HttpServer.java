package com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author liangkuai
 * @date 2018/8/28
 */
public class HttpServer {

    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    private boolean shutdown = false;

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;

        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建 request
                Request request = new Request(input);
                request.parse();

                // 创建 response
                Response response = new Response(output);
                response.setRequest(request);

                // 判断请求的资源类型
                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                // 关闭 socket
                socket.close();

                // 关闭命令
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }
}
