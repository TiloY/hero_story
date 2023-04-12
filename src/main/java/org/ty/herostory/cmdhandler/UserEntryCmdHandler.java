package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.BroadCaster;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 处理入场信息
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(UserEntryCmdHandler.class);

    /**
     * 处理入场消息
     *
     * @param ctx
     * @param cmd
     */
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        log.info("UserEntryCmdHandler handle : {}", cmd);
        // 从指令对象中获取用户id 和英雄形象

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(cmd.getUserId());
        resultBuilder.setHeroAvatar(cmd.getHeroAvatar());

        User user = new User();
        user.setUserId(cmd.getUserId());
        user.setHeroAvatar(cmd.getHeroAvatar());
        UserManager.addUser(user);

        //将userId 和 channel 绑定
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(cmd.getUserId());

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

        //构建结果 群发
        BroadCaster.broadcast(newResult);
        log.info(" UserEntryCmdHandler broadcast :{}", newResult);
    }
}
