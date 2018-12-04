package org.look.kitty;

import org.look.kitty.connector.http.HttpRequest;
import org.look.kitty.connector.http.HttpResponse;

import java.io.IOException;

/**
 * @author liangkuai
 * @date 2018/9/1
 */
public class StaticResourceProcessor {

    public void process(HttpRequest request, HttpResponse response) {
        try {
            response.sendStaticResource();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
