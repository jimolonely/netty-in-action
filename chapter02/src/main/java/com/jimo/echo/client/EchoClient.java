package com.jimo.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * EchoClient
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/16 16:18
 */
public class EchoClient {
    /**
     * host
     */
    final private String host;
    /**
     * port
     */
    final private int port;

    /**
     * constructor
     */
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * start
     */
    public void start() throws InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            final Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            final ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * main
     */
    public static void main(String[] args) throws InterruptedException {
        new EchoClient("127.0.0.1", 8080).start();
    }
}
