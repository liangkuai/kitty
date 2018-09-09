package com.server.connector.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liangkuai
 * @date 2018/9/9
 */
public class SocketInputStream extends InputStream {

    private InputStream in;

    /**
     * 内部缓存
     */
    private byte[] buf;


    /**
     *
     * @param in 输入流
     * @param bufferSize 内部缓存大小
     */
    public SocketInputStream(InputStream in, int bufferSize) {
        this.in = in;
        this.buf = new byte[bufferSize];
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
