package org.ty.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;
import org.ty.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        BroadCaster.addChannel((ctx.channel()));
    }

    /**
     * 处理离线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        BroadCaster.removeChannel(ctx.channel());
        //拿到用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }
        UserManager.removeUserById(userId);
        GameMsgProtocol.UserQuitResult newResult = GameMsgProtocol.UserQuitResult
                .newBuilder()
                .setQuitUserId(userId)
                .build();
        //将离场消息广播出去
        BroadCaster.broadcast(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到客户端消息，msgClazz=  " + msg.getClass().getName() + ", msg = " + msg);

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            // 从指令对象中获取用户id 和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(cmd.getUserId());
            resultBuilder.setHeroAvatar(cmd.getHeroAvatar());

            User user = new User();
            user.setUserId(cmd.getUserId());
            user.setHeroAvatar(cmd.getHeroAvatar());
            UserManager.addUser(user);

            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

            //构建结果 群发
            BroadCaster.broadcast(newResult);
            log.info("====消息群发了====");
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
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
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;
            GameMsgProtocol.UserMoveToResult newResult = GameMsgProtocol.UserMoveToResult
                    .newBuilder()
                    .setMoveUserId(userId)
                    .setMoveToPosX(cmd.getMoveToPosX())
                    .setMoveToPosY(cmd.getMoveToPosY())
                    .build();
            BroadCaster.broadcast(newResult);
        }
    }
}
