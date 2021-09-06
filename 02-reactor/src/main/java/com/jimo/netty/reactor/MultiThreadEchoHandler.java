package com.jimo.netty.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadEchoHandler implements Runnable {
    final SocketChannel channel;
    final SelectionKey sk;

    final ByteBuffer buf = ByteBuffer.allocate(1024);
    static final int RECIVING = 0, SENDING = 1;
    int state = RECIVING;

    static ExecutorService pool = Executors.newFixedThreadPool(4);

    public MultiThreadEchoHandler(SocketChannel channel, Selector selector) throws IOException {
        this.channel = channel;
        channel.configureBlocking(false);
        this.sk = channel.register(selector, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        pool.execute(new AsyncTask());
    }

    class AsyncTask implements Runnable {

        @Override
        public void run() {
            System.out.println("这是处理Thread：" + Thread.currentThread());
            MultiThreadEchoHandler.this.asyncRun();
        }
    }

    public synchronized void asyncRun() {
        try {
            if (state == SENDING) {
                channel.write(buf);
                buf.clear();
                sk.interestOps(SelectionKey.OP_READ);
                state = RECIVING;
                System.out.println("已经写回去了");
            } else if (state == RECIVING) {
                int len;
                while ((len = channel.read(buf)) > 0) {
                    System.out.println(new String(buf.array(), 0, len));
                }
                buf.flip();
                sk.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
