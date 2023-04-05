package org.ty.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 负责广播
 * 这就是相当于一个工具类
 */
public final class BroadCaster {
    // 客户端信道数组 ， 这个一定要用static  否则无法实现群发 原因是： 每次客户连接竟来的时候每次都会在serverMain 里边new 的新的对象  所以
    // 需要使用static 保证唯一
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 私有构造
    private BroadCaster() {
    }

    /**
     * 添加信道
     * @param ch
     */
    public static void addChannel(Channel ch) {
        _channelGroup.add(ch);
    }
    /**
     * 移除信道
     * @param ch
     */
    public static void removeChannel(Channel ch) {
        _channelGroup.remove(ch);
    }

    /**
     * 广播消息
     * @param msg
     */
    public static void broadcast(Object msg){
        if(null == msg){
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
}
