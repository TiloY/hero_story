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

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();

        // 廣播消息
        BroadCaster.broadcast(newResult);

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder2 = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder2.setTargetUserId(targetUserId);
        resultBuilder2.setSubtractHp(10);

        GameMsgProtocol.UserSubtractHpResult newResult2 = resultBuilder2.build();
        BroadCaster.broadcast(newResult2);
    }
}
