package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.model.MoveState;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;

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

            //在这里构建一个用户信息
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currentUser.getUserId());
            userInfoBuilder.setHeroAvatar(currentUser.getHeroAvatar());

            // 获取移动状态
            MoveState moveState = currentUser.getMoveState();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder
                    mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(moveState.getFromPosX());
            mvStateBuilder.setFromPosY(moveState.getFromPosY());
            mvStateBuilder.setToPosX(moveState.getToPosX());
            mvStateBuilder.setToPosY(moveState.getToPosY());
            mvStateBuilder.setStartTime(moveState.getStartTime());
            // 将移动状态设置到用户身上去
            userInfoBuilder.setMoveState(mvStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
