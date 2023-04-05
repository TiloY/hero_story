package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 处理谁在场
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(WhoElseIsHereCmdHandler.class);

    /**
     * 初始还有谁在场消息
     *
     * @param ctx
     * @param msg
     */
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd msg) {
        log.info("WhoElseIsHereCmdHandler handle : {}", msg);
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        for (User currentUser : UserManager.listUser()) {
            if (null == currentUser) {
                continue;
            }
            GameMsgProtocol.WhoElseIsHereResult.UserInfo
                    userInfo = GameMsgProtocol.WhoElseIsHereResult
                    .UserInfo.newBuilder()
                    .setUserId(currentUser.getUserId())
                    .setHeroAvatar(currentUser.getHeroAvatar())
                    .build();
            resultBuilder.addUserInfo(userInfo);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
