package org.look.kitty.connector.http;

import org.look.kitty.util.ParameterMap;
import org.look.kitty.util.RequestUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * @author liangkuai
 * @date 2018/9/9
 */
public class HttpRequest implements HttpServletRequest {

    private InputStream input;



    public HttpRequest(InputStream input) {
        this.input = input;
    }


    /*********************************** request line ************************************/

    private String method;
    private String requestURI;
    private String queryString;
    private String protocol;

    /**
     * method
     */
    @Override
    public String getMethod() {
        return this.method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * request url
     */
    @Override
    public String getRequestURI() {
        return this.requestURI;
    }
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * request url, query string
     */
    @Override
    public String getQueryString() {
        return this.queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * protocol
     */
    @Override
    public String getProtocol() {
        return this.protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }






    /************************************* header ************************************/

    private Map<String, List<String>> headers = new HashMap<>();

    private String requestedSessionId;
    /**
     * URL 中是否有 session id
     */
    private boolean requestedSessionURL;
    /**
     * Cookie 中是否有 session id
     */
    private boolean requestedSessionCookie;

    private List<Cookie> cookies = new ArrayList<>();

    private int contentLength;
    private String contentType;



    public void addHeader(String name, String value) {
        name = name.toLowerCase();
        synchronized (headers) {
            List<String> values = headers.get(name);
            if (values == null) {
                values = new ArrayList<>();
                headers.put(name, values);
            }
            values.add(value);
        }
    }


    /**
     * session id
     */
    @Override
    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }
    public void setRequestedSessionId(String requestSessionId) {
        this.requestedSessionId = requestSessionId;
    }

    public boolean isRequestedSessionURL() {
        return requestedSessionURL;
    }
    public void setRequestedSessionURL(boolean requestedSessionURL) {
        this.requestedSessionURL = requestedSessionURL;
    }

    public boolean isRequestedSessionCookie() {
        return requestedSessionCookie;
    }
    public void setRequestedSessionCookie(boolean requestedSessionCookie) {
        this.requestedSessionCookie = requestedSessionCookie;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }


    /**
     * cookie
     */
    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }


    /**
     * content-length
     */
    @Override
    public int getContentLength() {
        return this.contentLength;
    }
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * content-type
     */
    @Override
    public String getContentType() {
        return this.contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }



    /************************************* header ************************************/

    /**
     * 查询字符串是否被解析过
     */
    private boolean parsed = false;

    /**
     * 参数，包括：
     *
     * 1. start line 中 request url 的 query string 部分
     * 2. body 中的参数
     */
    private ParameterMap parameters = null;


    @Override
    public String getParameter(String name) {
        parseParameters();
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }


    /**
     * 获取参数
     */
    private void parseParameters() {
        // 被解析过则结束
        if (parsed)
            return;

        ParameterMap results = parameters;
        if (results == null) {
            results = new ParameterMap();
            results.setLocked(false);
        }

        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }

        String queryString = getQueryString();
        try {
            RequestUtil.parseParameters(results, queryString, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        // TODO: 解析 body，当且仅当：
        // 1. post method
        // 2. content-type: application/x-www-form-urlencoded



        // 锁定 params
        results.setLocked(true);
        parsed = true;
        parameters = results;
    }






    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }
}
