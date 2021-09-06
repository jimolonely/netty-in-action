package com.jimo.netty.jnio;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioChannelTest extends TestCase {

    private final static int CAPACITY = 1024;
    private String testFile = "/home/jack/workspace/git/netty-in-action/01-java-nio/src/test/java/com/jimo/netty/jnio/NioChannelTest.java";

    public void testFileChannel() throws IOException {
        nioCopyFile(testFile, "/tmp/pom.xml");
    }

    private void nioCopyFile(String srcPath, String destPath) throws IOException {
        File src = new File(srcPath);
        File dest = new File(destPath);
        assert dest.exists() || dest.createNewFile();
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dest);
             FileChannel inChannel = fis.getChannel();
             FileChannel outChannel = fos.getChannel()) {
            int len = -1;
            ByteBuffer buf = ByteBuffer.allocate(CAPACITY);
            while ((len = inChannel.read(buf)) != -1) {
                buf.flip();
                int outLen;
                while ((outLen = outChannel.write(buf)) != 0) {
                    System.out.println("写入的字节数：" + outLen);
                }
                buf.clear();
            }
            outChannel.force(true);
        }
    }

    /**
     * UDP 客户端测试
     * 服务端：nc -ul 8080
     */
    public void testUDPChannel() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.allocate(CAPACITY);
        String s = "Hello UDP 测试";
        buf.put((System.currentTimeMillis() + " >> " + s).getBytes(StandardCharsets.UTF_8));
        buf.flip();
        channel.send(buf, new InetSocketAddress("127.0.0.1", 8080));
        buf.clear();
        channel.close();
    }

    public void testUDPServerChannel() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress("127.0.0.1", 8080));
        System.out.println("UDP服务启动成功");
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        while (selector.select() > 0) {
            ByteBuffer buf = ByteBuffer.allocate(CAPACITY);
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isReadable()) {
                    SocketAddress client = channel.receive(buf);
                    buf.flip();
                    System.out.println("收到：" + new String(buf.array(), 0, buf.limit()));
                    buf.clear();
                }
            }
            it.remove();
        }
        selector.close();
        channel.close();
    }
}