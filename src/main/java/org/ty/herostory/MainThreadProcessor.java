package org.ty.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.cmdhandler.CmdHandlerFactory;
import org.ty.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 主线程处理器
 */
public final class MainThreadProcessor {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(MainThreadProcessor.class);
    /**
     * 单例对象
     */
    private static final MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 单线程线程池
     */
    private static final ExecutorService _es = Executors.newSingleThreadExecutor((r)-> {
        Thread newThread = new Thread(r);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    /**
     * 私有构造
     */
    private MainThreadProcessor() {
    }


    /**
     * 获取单例对象
     *
     * @return 主线程处理器
     */
    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx 客户端的信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (null == ctx ||
                null == msg) {
            return;
        }

        this._es.submit(() -> {
            //获取消息类
            Class<? extends GeneratedMessageV3> msgClazz = msg.getClass();

            //获取指令处理器
            ICmdHandler<? extends GeneratedMessageV3>
                    cmdHandler = CmdHandlerFactory.create(msgClazz);

            if (null == cmdHandler) {
                log.error(
                        "未找到相对应的指令处理器， msgClazz = {} ",
                        msgClazz.getName()
                );
                return;
            }

            try {
                //处理指令  不要因为异常让线程断掉
                cmdHandler.handle(ctx, cast(msg));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }


    /**
     * 小技巧 -- 转型消息对象
     *
     * @param msg
     * @param <TCmd>
     * @return
     */
    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        }
        return (TCmd) msg;
    }

}
