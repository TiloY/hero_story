package org.ty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理器接口
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    void handle(ChannelHandlerContext ctx, TCmd msg);

}
