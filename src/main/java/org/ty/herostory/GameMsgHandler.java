package org.ty.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(GameMsgHandler.class);
    // 客户端信道数组 ， 这个一定要用static  否则无法实现群发 原因是： 每次客户连接竟来的时候每次都会在serverMain 里边new 的新的对象  所以
    // 需要使用static 保证唯一
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //用户字典
    private static final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    /**
     * 处理离线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        _channelGroup.remove(ctx.channel());
        //拿到用户id
        Integer userId =(Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(null == userId){
            return;
        }
        _userMap.remove(userId);
        GameMsgProtocol.UserQuitResult newResult = GameMsgProtocol.UserQuitResult
                .newBuilder()
                .setQuitUserId(userId)
                .build();
        //将离场消息广播出去
        _channelGroup.writeAndFlush(newResult);
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
            _userMap.put(user.getUserId(), user);

            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

            //构建结果 群发
            _channelGroup.writeAndFlush(newResult);
            log.info("====消息群发了====");
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User currentUser : _userMap.values()) {
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
        }else if(msg instanceof GameMsgProtocol.UserMoveToCmd){
            Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if(null == userId){
                return;
            }
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg ;
            GameMsgProtocol.UserMoveToResult newResult = GameMsgProtocol.UserMoveToResult
                    .newBuilder()
                    .setMoveUserId(userId)
                    .setMoveToPosX(cmd.getMoveToPosX())
                    .setMoveToPosY(cmd.getMoveToPosY())
                    .build();
            _channelGroup.writeAndFlush(newResult);
        }
    }
}
