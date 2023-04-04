package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.ty.herostory.msg.GameMsgProtocol;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("====== channelRead ======= ");
        if (!((msg) instanceof BinaryWebSocketFrame)) {
            return;
        }

        ByteBuf byteBuf = ((BinaryWebSocketFrame) msg).content();
        // 读消息长度丢弃
        byteBuf.readShort();
        // 读取消息编号 代表我们的消息类型
        int msgCode = byteBuf.readShort();
        // 拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        GeneratedMessageV3 cmd = null ;

        switch (msgCode){
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                break ;

        }


        if(null != cmd ){
            ctx.fireChannelRead(cmd);
        }


    }
}
