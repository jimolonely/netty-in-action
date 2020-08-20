package com.jimo.bt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

import java.nio.ByteBuffer;
import java.util.Random;

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

    private Random random = new Random();

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
     * 访问里面的数据
     */
    public void byteBufCompositeArray() {
        final CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        final int length = compBuf.readableBytes();
        final byte[] array = new byte[length];
        compBuf.getBytes(compBuf.readerIndex(), array);
        handleArray(array, 0, array.length);
    }

    /**
     * 随机访问索引
     */
    public void byteBufAccessRelative() {
        final ByteBuf buffer = BYTE_FROM_SOMEWHERE;
        for (int i = 0; i < buffer.capacity(); i++) {
            final byte b = buffer.getByte(i);
            print("b={}", (char) b);
        }
    }

    /**
     * 读取所有数据
     */
    public void readAllData() {
        final ByteBuf buffer = BYTE_FROM_SOMEWHERE;
        while (buffer.isReadable()) {
            print("byte:{}", buffer.readByte());
        }
    }

    /**
     * 写数据
     */
    public void write() {
        final ByteBuf buffer = BYTE_FROM_SOMEWHERE;
        while (buffer.writableBytes() >= 4) {
            buffer.writeInt(random.nextInt());
        }
    }

    /**
     * 使用ByteProcessor来查找
     */
    public void byteProcess() {
        final ByteBuf buffer = BYTE_FROM_SOMEWHERE;
        // 寻找 \r
        int index = buffer.forEachByte(ByteProcessor.FIND_CR);
    }

    /**
     * 使用ByteBufProcessor来查找\r
     */
    @Deprecated
    private void byteBufProcessor() {
        final ByteBuf buffer = BYTE_FROM_SOMEWHERE;
        buffer.forEachByte(ByteBufProcessor.FIND_CR);
    }


    /**
     * 做一些处理
     */
    private void handleArray(byte[] array, int offset, int length) {
        //
    }
}
