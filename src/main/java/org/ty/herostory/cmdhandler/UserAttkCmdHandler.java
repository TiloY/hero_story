package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.BroadCaster;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 用戶攻擊處理器
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd>{
    private static final Logger log = LoggerFactory.getLogger(UserAttkCmdHandler.class);
    public static final String USER_ID = "userId";

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        log.info(" UserAttkCmdHandler 收到攻擊指令 ");
        if(null == ctx || null ==cmd){
            return;
        }

        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf(USER_ID)).get();
        Integer targetUserId = cmd.getTargetUserId();

        //構建對象
        GameMsgProtocol.UserAttkResult newResult = GameMsgProtocol
                .UserAttkResult
                .newBuilder()
                .setAttkUserId(attkUserId)
                .setTargetUserId(targetUserId)
                .build();
        // 廣播消息
        BroadCaster.broadcast(newResult);
    }
}
