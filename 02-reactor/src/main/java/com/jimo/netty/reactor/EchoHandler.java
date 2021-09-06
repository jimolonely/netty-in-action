package com.jimo.netty.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class EchoHandler implements Runnable {

    final SocketChannel channel;
    final SelectionKey key;
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    static final int RECIVING = 0, SENDING = 1;
    int state = RECIVING;

    public EchoHandler(SocketChannel channel, Selector selector) throws IOException {
        this.channel = channel;
        channel.configureBlocking(false);
        this.key = channel.register(selector, 0);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            if (state == SENDING) {
                channel.write(buf);
                buf.clear();
                key.interestOps(SelectionKey.OP_READ);
                state = RECIVING;
            } else if (state == RECIVING) {
                int len;
                while ((len = channel.read(buf)) > 0) {
                    System.out.println(new String(buf.array(), 0, len));
                }
                buf.flip();
                key.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
            // 不能关闭：
//            key.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
