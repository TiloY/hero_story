package org.ty.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 工程主函数
 */
public class ServerMain {

    public static void main(String[] args) {
        EventLoopGroup boosGroup = new NioEventLoopGroup(); //1.处理客服端的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//2.工作线程池

        ServerBootstrap b = new ServerBootstrap();
        b.group(boosGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),
                        new HttpObjectAggregator(65535),
                        new WebSocketServerProtocolHandler("/websocket"),
                        new GameMsgHandler()
                );
            }
        });

        try {
            ChannelFuture f = b.bind(12345).sync();

            if(f.isSuccess()){
                System.out.println("服务器启动成功");
            }

            f.channel().closeFuture().sync();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
