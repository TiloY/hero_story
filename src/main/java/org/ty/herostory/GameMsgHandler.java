package org.ty.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.ty.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    // 客户端信道数组 ， 这个一定要用static  否则无法实现群发 原因是： 每次客户连接竟来的时候每次都会在serverMain 里边new 的新的对象  所以
    // 需要使用static 保证唯一
    private static ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到客户端消息，msgClazz=  "+msg.getClass().getName() + ", msg = "+ msg);

        if(msg instanceof GameMsgProtocol.UserEntryCmd){
            // 从指令对象中获取用户id 和英雄形象
            GameMsgProtocol.UserEntryCmd cmd =   (GameMsgProtocol.UserEntryCmd) msg;
            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(cmd.getUserId());
            resultBuilder.setHeroAvatar(cmd.getHeroAvatar());

            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

            //构建结果 群发
            _channelGroup.writeAndFlush(newResult);
            System.out.println("=== 群发了==== ");

        }


    }
}
