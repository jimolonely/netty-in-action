package com.jimo.bt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

import static com.jimo.util.Output.print;

/**
 * ByteBufDemo
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/19 21:46
 */
public class ByteBufDemo {
    /**
     *
     */
    private final ByteBuf BYTE_FROM_SOMEWHERE = Unpooled.buffer(1024);

    /**
     * main
     */
    public static void main(String[] args) {

    }

    /**
     * 堆内字节
     */
    public void heapBuffer() {
        ByteBuf heapBuf = BYTE_FROM_SOMEWHERE;
        if (heapBuf.hasArray()) {
            final byte[] array = heapBuf.array();
            final int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            final int length = heapBuf.readableBytes();
            handleArray(array, offset, length);
        }
    }

    /**
     * 直接内存访问
     */
    public void directBuffer() {
        final ByteBuf directBuf = BYTE_FROM_SOMEWHERE;
        if (!directBuf.hasArray()) {
            final int length = directBuf.readableBytes();
            final byte[] array = new byte[length];
            directBuf.getBytes(directBuf.readerIndex(), array);
            handleArray(array, 0, length);
        }
    }

    /**
     * 组合模式,原生buffer使用
     */
    public void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        final ByteBuffer[] messages = {header, body};

        final ByteBuffer messages2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        messages2.put(header);
        messages2.put(body);
        messages2.flip();
    }

    /**
     * 使用CompositeByteBuf组合buffer
     */
    public void byteBufComposite() {
        final CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf header = BYTE_FROM_SOMEWHERE;
        ByteBuf body = BYTE_FROM_SOMEWHERE;
        messageBuf.addComponents(header, body);
        // ...
        messageBuf.removeComponent(0);
        for (ByteBuf buf : messageBuf) {
            print("buf:{}", buf.toString());
        }
    }

    /**
     * 做一些处理
     */
    private void handleArray(byte[] array, int offset, int length) {
        //
    }
}
