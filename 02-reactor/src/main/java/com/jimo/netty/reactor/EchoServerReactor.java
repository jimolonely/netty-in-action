package com.jimo.netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * 使用nc模拟客户端输入和接收
 * nc 127.0.0.1 8080
 */
public class EchoServerReactor implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverChannel;

    public EchoServerReactor() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        selector = Selector.open();
        SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        key.attach(new AcceptorHandler());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey key : selected) {
                    dispatch(key);
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void dispatch(SelectionKey key) {
        Runnable handler = (Runnable) key.attachment();
        if (handler != null) {
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    new EchoHandler(channel, selector);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new EchoServerReactor()).start();
    }
}
