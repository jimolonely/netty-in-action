package com.jimo.netty.jnio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioReceiveServer {

    private Charset charset = StandardCharsets.UTF_8;

    static class Client {
        String filename;
        long fileLength;
        long startTime;
        InetSocketAddress address;
        FileChannel outChannel;
    }

    private ByteBuffer buf = ByteBuffer.allocate(1024);
    Map<SelectableChannel, Client> clientMap = new HashMap<>(4);

    public void startServer() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器就绪...");
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) {
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    Client client = new Client();
                    client.address = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put(socketChannel, client);
                    System.out.println("客户端连接成功：" + client.address);
                } else if (key.isReadable()) {
                    processData(key);
                }
                it.remove();
            }
        }
    }

    private void processData(SelectionKey key) throws IOException {
        Client client = clientMap.get(key.channel());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int num;
        try {
            buf.clear();
            while ((num = socketChannel.read(buf)) > 0) {
                buf.flip();
                if (null == client.filename) {
                    // 先是文件名
                    String filename = charset.decode(buf).toString();
                    File dir = new File("/home/jack/tmp");
                    assert dir.exists() || dir.mkdir();
                    client.filename = filename;
                    String fullName = dir.getAbsolutePath() + File.separator + filename;
                    File file = new File(fullName);
                    client.outChannel = new FileOutputStream(file).getChannel();
                } else if (0 == client.fileLength) {
                    // 然后是文件长度
                    client.fileLength = buf.getLong();
                    client.startTime = System.currentTimeMillis();
                    System.out.println("传输开始...");
                } else {
                    // 最后是文件内容
                    client.outChannel.write(buf);
                }
                buf.clear();
            }
            key.cancel();
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            return;
        }
        if (num == -1) {
            // 结束了
            client.outChannel.close();
            System.out.println("上传完毕");
            key.cancel();
            System.out.printf("文件成功接收，名称为：%s,大小为：%s, 耗时为：%s ms%n",
                    client.filename, client.fileLength, (System.currentTimeMillis() - client.startTime));
        }
    }

    public static void main(String[] args) throws IOException {
        new NioReceiveServer().startServer();
    }
}
