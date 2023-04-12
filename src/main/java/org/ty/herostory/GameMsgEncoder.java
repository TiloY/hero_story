package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == msg ||
                !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        Class<?> msgClazz = msg.getClass();

        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(msgClazz);
        if (msgCode <= -1) {
            log.error("无法识别的消息类型,msgClazz= " + msg.getClass().getName());
            return;
        }
        //将消息转成字节数组
        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
        //申请一个BytBuf 对象
        ByteBuf byteBuf = ctx.alloc().buffer();
        //写消息长度
        byteBuf.writeShort((short) 0);
        //写消息类型
        byteBuf.writeShort((short)  msgCode);
        //写消息体
        byteBuf.writeBytes(msgBody);
        //使用固定载体把消息写出去
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }
}
