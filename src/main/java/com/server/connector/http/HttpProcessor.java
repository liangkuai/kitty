package com.server.connector.http;

import com.server.ServletProcessor;
import com.server.StaticResourceProcessor;
import com.server.util.StringManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * processor
 *
 * @author liangkuai
 * @date 2018/9/9
 */
public class HttpProcessor {

    private StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private HttpConnector connector;

    private HttpRequest request;
    private HttpResponse response;


    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void process(Socket socket) {
        SocketInputStream input = null;
        OutputStream output = null;

        try {
            input = new SocketInputStream(socket.getInputStream(), 2048);
            output = socket.getOutputStream();

            request = new HttpRequest(input);
            response = new HttpResponse(output);
            response.setRequest(request);

            response.setHeader("Server", "Servlet Container");

            parseRequest(input, output);
            parseHeaders(input);

            if (request.getRequestURI().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parseRequest(SocketInputStream input, OutputStream output) {

    }


    private void parseHeaders(SocketInputStream input) {

    }
}
