package com.jimo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * netty实现的OIO服务
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/8/18 8:29
 */
public class NettyOioServer {

    /**
     * 服务
     */
    public void serve(int port) throws InterruptedException {
        ByteBuf msg = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\n".getBytes()));

        final OioEventLoopGroup group = new OioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(msg.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            final ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
