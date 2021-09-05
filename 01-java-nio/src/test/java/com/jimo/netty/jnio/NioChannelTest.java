package com.jimo.netty.jnio;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioChannelTest extends TestCase {

    private final static int CAPACITY = 1024;

    public void testFileChannel() throws IOException {
        nioCopyFile("/home/jack/workspace/git/netty-in-action/01-java-nio/src/test/java/com/jimo/netty/jnio/NioChannelTest.java", "/tmp/pom.xml");
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
}