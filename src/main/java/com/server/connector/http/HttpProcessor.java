package com.server.connector.http;

import com.server.ServletProcessor;
import com.server.StaticResourceProcessor;
import com.server.util.StringManager;

import javax.servlet.ServletException;
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
    private HttpRequestLine requestLine = new HttpRequestLine();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void parseRequest(SocketInputStream input, OutputStream output) throws ServletException {
        // 解析请求行
        input.readRequestLine(requestLine);

        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        String uri = null;
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

        // 校验请求行
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        } else if (requestLine.uriEnd < 1) {
            throw new ServletException("Missing HTTP request URI");
        }


    }


    private void parseHeaders(SocketInputStream input) {

    }
}
