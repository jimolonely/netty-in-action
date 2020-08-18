package com.jimo.plain;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static com.jimo.util.Output.print;

/**
 * 原生OIO实现
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/18 8:09
 */
public class PlainOioServer {

    /**
     * 服务
     */
    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        for (; ; ) {
            final Socket clientSocket = socket.accept();
            print("收到来自客户端{}的请求", clientSocket);
            new Thread(() -> {
                try (OutputStream out = clientSocket.getOutputStream()) {
                    out.write("Hi!\n".getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
