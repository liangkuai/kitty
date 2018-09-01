package com.server;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * @author liangkuai
 * @date 2018/8/31
 */
public class ServletProcessor {

    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;

        File classPath = new File(Constants.WEB_STATIC);

        try {
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
