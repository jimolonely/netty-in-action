package com.jimo.netty.jnio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 服务端可以运行 nc -l 8080, 或启动{@link NioReceiveServer}
 */
public class NioSendClient {

    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
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
            ByteBuffer buf = ByteBuffer.allocate(1024);
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

    public static void main(String[] args) throws IOException {
        new NioSendClient().sendFile("/home/jack/.bashrc");
    }
}
