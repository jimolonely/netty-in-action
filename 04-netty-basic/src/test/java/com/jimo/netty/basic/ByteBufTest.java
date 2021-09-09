package com.jimo.netty.basic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ByteBufTest {

    final static Charset UTF8 = StandardCharsets.UTF_8;

    @Test
    public void testBufType() {
//        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
//        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();//我ubuntu下默认是 直接内存
        buf.writeBytes("你好呀".getBytes(UTF8));
        if (buf.hasArray()) {
            byte[] array = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int len = buf.readableBytes();
            System.out.println("堆buf:" + new String(array, offset, len, UTF8));
        } else {
            int len = buf.readableBytes();
            byte[] array = new byte[len];
            buf.getBytes(buf.readerIndex(), array);
            System.out.println("直接内存buf: " + new String(array, UTF8));
        }
        buf.release();
    }

    @Test
    public void testCompositeBuf() {
        CompositeByteBuf buf = Unpooled.compositeBuffer(3);
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{1, 2, 3}));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{4}));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{5, 6}));
        // 合并
        ByteBuffer nioBuf = buf.nioBuffer(0, 6);
        byte[] bytes = nioBuf.array();
        System.out.print("bytes=");
        for (byte b : bytes) {
            System.out.print(b);
        }
        buf.release();
    }
}