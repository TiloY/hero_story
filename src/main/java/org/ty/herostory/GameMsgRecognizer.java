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
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(GameMsgRecognizer.class);
    /**
     * 常量维护
     */
    public static final String GET_DEFAULT_INSTANCE = "getDefaultInstance";

    /**
     * 消息字典
     */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();
    private static final Map<Class<?>, Integer> _msgTypeAndMsgCodeMap = new HashMap<>();

    /**
     * 私有构造
     */
    private GameMsgRecognizer() {
    }

    /**
     * 动态添加字典 --反射
     */
    /**
     * 初始化
     */
    static public void init() {
        log.info("=== 完成消息编号与消息体的映射 ===");

        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : innerClazzArray) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                // 如果不是消息,
                continue;
            }

            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                // 获取消息编号
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    Object returnObj = innerClazz.getDeclaredMethod(GET_DEFAULT_INSTANCE).invoke(innerClazz);

                    log.info(
                            "关联 {} <==> {}",
                            innerClazz.getName(),
                            msgCode.getNumber()
                    );

                    _msgCodeAndMsgBodyMap.put(
                            msgCode.getNumber(),
                            (GeneratedMessageV3) returnObj
                    );

                    _msgTypeAndMsgCodeMap.put(
                            innerClazz,
                            msgCode.getNumber()
                    );
                } catch (Exception ex) {
                    // 记录错误日志
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }


    /**
     * 根据消息类型拿不同的消息构建器
     *
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(Integer msgCode) {
        if ( msgCode < 0) {
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
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
    public static Integer getMsgCodeByMsgClazz(Class<?> clazz) {

        if (null == clazz) {
            return -1;
        }
        Integer msgCode = _msgTypeAndMsgCodeMap.get(clazz);
        if(null != msgCode){
            return msgCode.intValue();
        }

        return -1 ;
    }


}
