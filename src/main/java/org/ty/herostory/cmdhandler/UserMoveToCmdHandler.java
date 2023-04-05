package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.BroadCaster;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 处理移动消息
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(UserMoveToCmdHandler.class);

    /**
     * 处理移动消息
     * @param ctx
     * @param msg
     */
    public  void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        log.info("UserMoveToCmdHandler handle : {}",msg);
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }
        GameMsgProtocol.UserMoveToCmd cmd = msg;
        GameMsgProtocol.UserMoveToResult newResult = GameMsgProtocol.UserMoveToResult
                .newBuilder()
                .setMoveUserId(userId)
                .setMoveToPosX(cmd.getMoveToPosX())
                .setMoveToPosY(cmd.getMoveToPosY())
                .build();
        BroadCaster.broadcast(newResult);
    }

}
