package com.jimo.netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadEchoServerReactor {
    ServerSocketChannel serverChannel;
    AtomicInteger next = new AtomicInteger(0);
    Selector[] selectors = new Selector[2];
    SubReactor[] subReactors;

    MultiThreadEchoServerReactor() throws IOException {
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(8080));
        serverChannel.configureBlocking(false);

        // 第一个选择器负责监听新连接事件
        SelectionKey sk = serverChannel.register(selectors[0], SelectionKey.OP_ACCEPT);
        sk.attach(new AcceptorHandler());
        SubReactor subReactor1 = new SubReactor(selectors[0]);
        SubReactor subReactor2 = new SubReactor(selectors[1]);
        subReactors = new SubReactor[]{subReactor1, subReactor2};
    }

    static class SubReactor implements Runnable {
        final Selector selector;

        SubReactor(Selector selector) {
            this.selector = selector;
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
            System.out.println("我是SubReactor：" + Thread.currentThread());
            Runnable handler = (Runnable) key.attachment();
            if (handler != null) {
                handler.run();
            }
        }
    }

    class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    new MultiThreadEchoHandler(channel, selectors[next.get()]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (next.incrementAndGet() == selectors.length) {
                next.set(0);
            }
        }
    }

    private void startService() {
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }

    public static void main(String[] args) throws IOException {
        new MultiThreadEchoServerReactor().startService();
    }
}
