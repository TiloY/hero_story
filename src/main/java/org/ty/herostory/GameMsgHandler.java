package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.cmdhandler.CmdHandlerFactory;
import org.ty.herostory.cmdhandler.ICmdHandler;
import org.ty.herostory.model.UserManager;
import org.ty.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        BroadCaster.addChannel(ctx.channel());
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

        Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(null == userId){
            return;
        }

        //移除用户
        UserManager.removeUserById(userId);

        // 广播用户离场的消息
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();

        BroadCaster.broadcast(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof  GeneratedMessageV3 ){
            MainThreadProcessor.getInstance().process(ctx, (GeneratedMessageV3) msg);
        }
    }
}
