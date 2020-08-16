package com.jimo.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import static com.jimo.util.Output.print;

/**
 * EchoServer
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/16 15:58
 */
public class EchoServer {
    /**
     * port
     */
    private final int port;

    /**
     * 构造
     */
    public EchoServer(int port) {
        this.port = port;
    }

    /**
     * start
     */
    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        final NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            final ChannelFuture f = b.bind().sync();
            print("服务器开始监听连接：{}", f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * main
     */
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new EchoServer(port).start();
    }
}
