package org.ty.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(GameMsgDecoder.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("GameMsgDecoder channelRead:{},msg:{}",ctx,msg);
        if (!((msg) instanceof BinaryWebSocketFrame)) {
            return;
        }

        ByteBuf byteBuf = ((BinaryWebSocketFrame) msg).content();
        // 读消息长度丢弃
        byteBuf.readShort();
        // 读取消息编号 代表我们的消息类型
        int msgCode = byteBuf.readShort();

        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (null == msgBuilder) {
            log.error("无法识别的消息，msgCode= ",msgCode);
            return;
        }

        // 拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);


        msgBuilder.clear();
        Message newMsg = msgBuilder.mergeFrom(msgBody).build();

        if (null != newMsg) {
            ctx.fireChannelRead(newMsg);
        }


    }
}
