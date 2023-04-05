package org.ty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 指令处理工厂
 * -- 迪米特法则
 * -- A - B  尽量少的耦合
 * -- final 不要继承我的工厂
 */
public final class CmdHandlerFactory {
    //私有工厂
    private CmdHandlerFactory() {
    }

    /**
     * 工厂模式
     * @param msg
     * @return
     */
    public static ICmdHandler<? extends GeneratedMessageV3> create(Object msg) {
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            return new UserEntryCmdHandler();
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            return new WhoElseIsHereCmdHandler();
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            return new UserMoveToCmdHandler();
        }
        return null;
    }

}
