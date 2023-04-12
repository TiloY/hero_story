package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.BroadCaster;
import org.ty.herostory.model.MoveState;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;

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
     * @param cmd
     */
    public  void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        log.info("UserMoveToCmdHandler handle : {}",cmd);
        if(null == ctx || null == cmd){
            return;
        }
        // 获取用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        User moveUser = UserManager.getUserById(userId);
        if( null == moveUser){
            return;
        }

        // 设置移动状态
        MoveState mvState = moveUser.getMoveState() ;
        // 设置位置和时间
        mvState.setFromPosX(cmd.getMoveFromPosX());
        mvState.setToPosY(cmd.getMoveFromPosY());
        mvState.setToPosX(cmd.getMoveToPosX());
        mvState.setToPosY(cmd.getMoveToPosY());
        mvState.setStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult newResult = GameMsgProtocol.UserMoveToResult
                .newBuilder()
                .setMoveUserId(userId)
                .setMoveFromPosX(mvState.getFromPosX())
                .setMoveFromPosY(mvState.getFromPosY())
                .setMoveToPosX(mvState.getFromPosX())
                .setMoveToPosY(mvState.getFromPosY())
                .setMoveStartTime(mvState.getStartTime())
                .build();

        BroadCaster.broadcast(newResult);
    }

}
