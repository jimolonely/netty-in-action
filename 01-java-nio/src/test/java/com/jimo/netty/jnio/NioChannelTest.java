package com.jimo.netty.jnio;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NioChannelTest extends TestCase {

    private final static int CAPACITY = 1024;
    private String testFile = "/home/jack/workspace/git/netty-in-action/01-java-nio/src/test/java/com/jimo/netty/jnio/NioChannelTest.java";

    public void testFileChannel() throws IOException {
        nioCopyFile(testFile, "/tmp/pom.xml");
    }

    private void nioCopyFile(String srcPath, String destPath) throws IOException {
        File src = new File(srcPath);
        File dest = new File(destPath);
        if (!dest.exists()) {
            dest.createNewFile();
        }
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
     * 服务端可以运行 nc -l 8080
     */
    public void testSocketChannel() throws IOException {
        File file = new File(testFile);
        FileInputStream fis = new FileInputStream(file);
        try (FileChannel inChannel = fis.getChannel();
             SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("127.0.0.1", 8080));
            while (!sc.finishConnect()) {
                System.out.println("连接等待中...");
            }
            System.out.println("连接成功");
            ByteBuffer bufName = StandardCharsets.UTF_8.encode(file.getName());
            sc.write(bufName);
            // 文件长度
            ByteBuffer buf = ByteBuffer.allocate(CAPACITY);
            buf.putLong(file.length());
            buf.flip();
            sc.write(buf);
            buf.clear();

            int len;
            int progress = 0;
            while ((len = inChannel.read(buf)) > 0) {
                buf.flip();
                sc.write(buf);
                buf.clear();
                progress += len;
                System.out.println("传输进度：" + (100.0 * progress / file.length()) + "%");
            }
            if (len == -1) {
                sc.shutdownOutput();
            }
        }
    }
}