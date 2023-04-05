package org.ty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.ty.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令处理工厂
 * -- 迪米特法则
 * -- A - B  尽量少的耦合
 * -- final 不要继承我的工厂
 */
public final class CmdHandlerFactory {
    //定义一个字典
    private static Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    //私有工厂
    private CmdHandlerFactory() {
    }

    /**
     * 填充字典
     * 这样每个 handler 就是一个单例
     * 我们的 handler 是不涉及到状态的  所以 。。
     */
    public static void init() {
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
    }

    /**
     * @param clazz
     * @return
     */
    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return _handlerMap.get(clazz);
    }

}
