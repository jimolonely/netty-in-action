package com.jimo.netty.jnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class DiscardClient {

    public static void startClient() throws IOException {
        SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        sc.configureBlocking(false);
        while (!sc.finishConnect()) {
            System.out.println("正在连接...");
        }
        System.out.println("连接成功");
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("你好".getBytes(StandardCharsets.UTF_8));
        buf.flip();
        sc.write(buf);
        sc.shutdownOutput();
        sc.close();
    }

    public static void main(String[] args) throws IOException {
        startClient();
    }
}
