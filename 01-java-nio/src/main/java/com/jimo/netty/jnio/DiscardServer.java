package com.jimo.netty.jnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class DiscardServer {

    public static void startServer() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel sc = ServerSocketChannel.open();
        sc.configureBlocking(false);
        sc.bind(new InetSocketAddress("127.0.0.1", 8080));

        sc.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = sc.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("收到连接");
                } else if (key.isReadable()) {
                    System.out.println("开始读数据");
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = socketChannel.read(buf)) > 0) {
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                    socketChannel.close();
                }
                it.remove();
            }
        }
        sc.close();
    }

    /**
     * 客户端可以使用nc模拟： nc 127.0.0.1 8080
     */
    public static void main(String[] args) throws IOException {
        startServer();
    }
}
