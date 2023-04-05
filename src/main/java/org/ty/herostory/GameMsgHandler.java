package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.cmdhandler.ICmdHandler;
import org.ty.herostory.cmdhandler.UserEntryCmdHandler;
import org.ty.herostory.cmdhandler.UserMoveToCmdHandler;
import org.ty.herostory.cmdhandler.WhoElseIsHereCmdHandler;
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

        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = null;
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            cmdHandler = new UserEntryCmdHandler();
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            cmdHandler = new WhoElseIsHereCmdHandler();
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            cmdHandler = new UserMoveToCmdHandler();
        }

        if (null != cmdHandler) {
            cmdHandler.handle(ctx, this.cast(msg));
        }
    }

    /**
     * 小技巧 -- 转型消息对象
     *
     * @param msg
     * @param <TCmd>
     * @return
     */
    private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        }
        return (TCmd) msg;
    }


}
