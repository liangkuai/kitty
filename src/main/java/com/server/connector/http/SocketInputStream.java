package com.server.connector.http;

import com.server.util.StringManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liangkuai
 * @date 2018/9/9
 */
public class SocketInputStream extends InputStream {

    private static StringManager sm = StringManager.getManager(Constants.PACKAGE);

    /**
     * CR（13）
     */
    private static final byte CR = (byte) '\r';

    /**
     * LF（10）
     */
    private static final byte LF = (byte) '\n';

    /**
     * Space（32）
     */
    private static final byte SP = (byte) ' ';

    /**
     * HT（9）
     */
    private static final byte HT = (byte) '\t';

    /**
     * Colon（58）
     */
    private static final byte COLON = (byte) ':';



    private InputStream in;

    /**
     * 内部缓存
     */
    private byte[] buf;

    private int count;

    private int pos;


    /**
     *
     * @param in 输入流
     * @param bufferSize 内部缓存大小
     */
    public SocketInputStream(InputStream in, int bufferSize) {
        this.in = in;
        this.buf = new byte[bufferSize];
    }


    /**
     * 解析请求行
     *
     * ---------------------
     * method uri protocol
     * [CR][LF]
     * ---------------------
     */
    void readRequestLine(HttpRequestLine requestLine) throws IOException {
        // method

        int maxRead = requestLine.method.length;
        int readCount = 0;

        boolean space = false;
        while (!space) {
            // method 字符数组即将读满。对其进行扩容
            if (readCount >= maxRead) {
                // method 字符数组加倍扩容
                if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
                    char[] newMethod = new char[2 * maxRead];
                    System.arraycopy(requestLine.method, 0, newMethod, 0, maxRead);
                    requestLine.method = newMethod;
                    maxRead *= 2;
                } else {
                    // method 长度超过限制
                    throw new IOException(sm.getString("requestStream.readline.toolong"));
                }
            }

            // 判断 buffer 是否满
            if (pos >= count) {
                // buffer 满
                // 填充 buf，并试读一个子节
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }

            // 遇到空格结束
            if (buf[pos] == SP) {
                space = true;
            } else {
                requestLine.method[readCount++] = (char) (buf[pos] & 0xff);
            }
            pos++;
        }

        requestLine.methodEnd = readCount;


        // request URI

        maxRead = requestLine.uri.length;
        readCount = 0;

        space = false;
        while (!space) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_URI_SIZE) {
                    char[] newURI = new char[2 * maxRead];
                    System.arraycopy(requestLine.uri, 0, newURI, 0, maxRead);
                    requestLine.uri = newURI;
                    maxRead *= 2;
                } else {
                    throw new IOException(sm.getString("requestStream.readline.toolong"));
                }
            }

            // 判断 buffer 是否满
            if (pos >= count) {
                // buffer 满
                // 填充 buf，并试读一个子节
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }

            // 遇到空格结束
            if (buf[pos] == SP) {
                space = true;
            } else {
                requestLine.uri[readCount++] = (char) (buf[pos] & 0xff);
            }
            pos++;
        }

        requestLine.uriEnd = readCount;


        // protocol

        maxRead = requestLine.protocol.length;
        readCount = 0;

        boolean eof = false;
        while (!eof) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
                    char[] newProtocol = new char[2 * maxRead];
                    System.arraycopy(requestLine.protocol, 0, newProtocol, 0, maxRead);
                    requestLine.protocol = newProtocol;
                    maxRead *= 2;
                } else {
                    throw new IOException(sm.getString("requestStream.readline.toolong"));
                }
            }

            // 判断 buffer 是否满
            if (pos >= count) {
                // buffer 满
                // 填充 buf，并试读一个子节
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }

            // 遇到空格结束
            if (buf[pos] == CR) {
                // 跳过
            } else if (buf[pos] == LF) {
                eof = true;
            } else {
                requestLine.uri[readCount++] = (char) (buf[pos] & 0xff);
            }
            pos++;
        }

        requestLine.uriEnd = readCount ;
    }


    /**
     * 解析首部
     *
     * ---------
     * name: value
     * [CR][LF]
     * ---------
     */
    void readHeader(HttpHeader header) throws IOException {

        // header name

        int maxRead = header.name.length;
        int readCount = 0;

        boolean colon = false;
        while (!colon) {
            if (readCount >= maxRead) {
                if ((maxRead * 2) <= HttpHeader.MAX_NAME_SIZE) {
                    char[] newName = new char[maxRead * 2];
                    System.arraycopy(header.name, 0, newName, 0, maxRead);
                    header.name = newName;
                    maxRead *= 2;
                } else {
                    throw new IOException(sm.getString("requestStream.readline.toolong"));
                }
            }

            if (pos >= count) {
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }

            // 当前字符是否是冒号
            if (buf[pos] == COLON) {
                // 遇到冒号，读完 header name
                colon = true;
            } else {
                header.name[readCount++] = (char) (buf[pos] & 0xff);
            }
            pos++;
        }

        header.nameEnd = readCount - 1;

        // header value
        maxRead = header.value.length;
        readCount = 0;

        // value 前的空格
        boolean space = true;
        while (space) {
            if (pos >= count) {
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }
            if ((buf[pos] == SP) || (buf[pos] == HT)) {
                pos++;
            } else {
                space = false;
            }
        }

        boolean eof = false;
        while (!eof) {
            if (readCount >= maxRead) {
                if ((maxRead * 2) <= HttpHeader.MAX_VALUE_SIZE) {
                    char[] newValue = new char[maxRead * 2];
                    System.arraycopy(header.value, 0, newValue, 0, maxRead);
                    header.value = newValue;
                    maxRead *= 2;
                } else {
                    throw new IOException(sm.getString("requestStream.readline.toolong"));
                }
            }

            if (pos >= count) {
                if (read() == -1) {
                    throw new IOException(sm.getString("requestStream.readline.error"));
                }
                pos = 0;
            }

            if (buf[pos] == CR) {
                // 跳过
            } else if (buf[pos] == LF) {
                eof = true;
            } else {
                header.value[readCount++] = (char) (buf[pos] & 0xff);
            }
            pos++;
        }

        header.valueEnd = readCount;
    }







    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count)
                return -1;
        }
        return buf[pos++] & 0xff;
    }

    /**
     * 填满 buffer
     */
    private void fill() throws IOException {
        pos = 0;
        count = 0;
        int num = in.read(buf, 0, buf.length);
        if (num > 0) {
            count = num;
        }
    }
}