package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器工厂
 * 消息识别器
 * -- 目标是重构我们原始的  GameMsgDecoder
 * 1. 实现解码
 * 2.
 */
public final class GameMsgRecognizer {
    private static final Logger log = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息字典
     */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBoyMap = new HashMap<>();
    private static final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    private GameMsgRecognizer() {
    }

    public static void init() {
        _msgCodeAndMsgBoyMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgBoyMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgBoyMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());


        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
    }


    /**
     * 根据消息类型拿不同的消息构建器
     *
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(Integer msgCode) {
        if (msgCode == null || msgCode <= 0) {
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBoyMap.get(msgCode);
        if (null == msg) {
            return null;
        }
        return msg.newBuilderForType();
    }

    /**
     * 根据消息获取消息类型
     *
     * @param clazz
     * @return
     */
    public static Integer getMsgCodeByMsgClazz(Class<?> clazz)  {

        if (null == clazz) {
            return null;
        }
        return _msgClazzAndMsgCodeMap.get(clazz);
    }


}
