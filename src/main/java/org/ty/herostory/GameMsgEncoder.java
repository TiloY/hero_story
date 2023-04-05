package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.msg.GameMsgProtocol;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == msg ||
                !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }
        log.error("===GameMsgEncoder=== ");
        int msgCode = -1;
        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
            msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
            msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
            msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        }else {
            log.error("无法识别的消息类型,msgClazz= " + msg.getClass().getName());
            return;
        }
        byte[] byteArray = ((GeneratedMessageV3) msg).toByteArray();

        //申请一个BytBuf 对象
        ByteBuf byteBuf = ctx.alloc().buffer();
        //写消息长度
        byteBuf.writeShort((short) 0);
        //写消息类型
        byteBuf.writeShort((short) msgCode);
        //写消息体
        byteBuf.writeBytes(byteArray);
        //使用固定载体把消息写出去
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }
}
