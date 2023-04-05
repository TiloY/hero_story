package org.ty.herostory;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 消息识别器
 * -- 目标是重构我们原始的  GameMsgDecoder
 * 1. 实现解码
 * 2.
 */
public final class GameMsgRecognizer {
    private static final Logger log = LoggerFactory.getLogger(GameMsgRecognizer.class);

    private GameMsgRecognizer() {
    }

    /**
     * 根据消息类型拿不同的消息构建器
     *
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(Integer msgCode) {
        switch (msgCode) {
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                return GameMsgProtocol.UserEntryCmd.newBuilder();
            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                return GameMsgProtocol.WhoElseIsHereCmd.newBuilder();
            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                return GameMsgProtocol.UserMoveToCmd.newBuilder();
            default:
                return null;
        }
    }

    /**
     * 根据消息获取消息类型
     * @param msg
     * @return
     */
    public static Integer getMsgCodeByMsgClazz(Object msg) {
        if (msg instanceof GameMsgProtocol.UserEntryResult) {
            return GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
            return GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
            return GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
            return GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        } else {
            log.error("无法识别的消息类型,msgClazz= " + msg.getClass().getName());
            return null;
        }
    }


}
