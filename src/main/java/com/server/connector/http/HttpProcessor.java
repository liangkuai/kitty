package com.server.connector.http;

import com.server.ServletProcessor;
import com.server.StaticResourceProcessor;
import com.server.util.RequestUtil;
import com.server.util.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * processor
 *
 * 负责处理请求
 *
 * @author liangkuai
 * @date 2018/9/9
 */
public class HttpProcessor {

    private StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private HttpConnector connector;

    /**
     * 请求行
     */
    private HttpRequestLine requestLine = new HttpRequestLine();

    private HttpRequest request;
    private HttpResponse response;


    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    /**
     * 处理 socket 连接
     */
    public void process(Socket socket) {
        SocketInputStream input;
        OutputStream output;

        try {
            input = new SocketInputStream(socket.getInputStream(), 2048);
            output = socket.getOutputStream();

            request = new HttpRequest(input);
            response = new HttpResponse(output);

            response.setRequest(request);
            response.setHeader("Server", "Servlet Container");

            // 解析请求
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


    /**
     * 解析请求
     */
    private void parseRequest(SocketInputStream input, OutputStream output)
            throws ServletException, IOException {
        // 解析请求行
        input.readRequestLine(requestLine);

        /**
         * 将 method 填充到 request
         */
        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        // 校验 method
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        } else if (requestLine.uriEnd < 1) {
            throw new ServletException("Missing HTTP request URI");
        }
        request.setMethod(method);

        // 将 method 填充到 request
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
        request.setProtocol(protocol);

        /**
         * 解析 URI
         */
        String uri = null;

        // 字符 '?' 前是 URI 路径，后是查询字符串
        int question = requestLine.index('?');
        if (question >= 0) {
            uri = new String(requestLine.uri, 0, question);
            request.setQueryString(new String(
                    requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
        } else {
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
            request.setQueryString(null);
        }

        /**
         * 对 URI 解析
         */
        if (!uri.startsWith("/")) {
            int pos = uri.indexOf("://");
            if (pos != -1) {

                pos = uri.indexOf('/', pos + 3);
                if (pos != -1) {
                    uri = uri.substring(pos);
                } else {
                    uri = "";
                }

            }
        }

        // 路径中的参数
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                request.setRequestedSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
        } else {
            // URI 路径中无 session id
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }

        // 标准化 URI
        String normalizedUri = normalizeURI(uri);

        if (normalizedUri != null) {
            request.setRequestURI(normalizedUri);
        } else {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
    }

    /**
     * 标准 URI 中的 path
     */
    private String normalizeURI(String path) {
        if (path == null)
            return null;
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.contains("%25"))
                || (normalized.contains("%2F"))
                || (normalized.contains("%2E"))
                || (normalized.contains("%5C"))
                || (normalized.contains("%2f"))
                || (normalized.contains("%2e"))
                || (normalized.contains("%5c"))) {
            return null;
        }

        if (normalized.equals("/."))
            return "/";

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null);  // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.contains("/..."))
            return (null);

        // Return the normalized path that we have completed
        return (normalized);
    }


    private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
        while (true) {
            HttpHeader header = new HttpHeader();
            input.readHeader(header);
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                } else {
                    throw new ServletException
                            (sm.getString("httpProcessor.parseHeaders.colon"));
                }
            }

            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            request.addHeader(name, value);

            // cookie
            if ("cookie".endsWith(name)) {
                Cookie[] cookies = RequestUtil.parseCookieHeader(value);
                for (Cookie cookie : cookies) {

                    if ("jsessionid".equals(cookie.getName())) {
                        if (!request.isRequestedSessionIdFromCookie()) {
                            request.setRequestedSessionId(cookie.getValue());
                            request.setRequestedSessionCookie(true);
                            request.setRequestedSessionURL(false);
                        }
                    }

                    request.addCookie(cookie);
                }
            }

            // content-length
            if ("content-length".equals(name)) {
                int n;
                try {
                    n = Integer.parseInt(value);
                } catch (Exception e) {
                    throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
                request.setContentLength(n);
            }

            // content-type
            if ("content-type".equals(name)) {
                request.setContentType(value);
            }
        }
    }
}
