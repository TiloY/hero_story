package org.ty.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令处理工厂
 * -- 迪米特法则
 * -- A - B  尽量少的耦合
 * -- final 不要继承我的工厂
 */
public final class CmdHandlerFactory {
    /**
     * 日志對象
     */
    private static final Logger log = LoggerFactory.getLogger(CmdHandlerFactory.class);
    public static final String HANDLE = "handle";
    //定义一个字典
    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();


    //私有工厂
    private CmdHandlerFactory() {
    }

    /**
     * 填充字典
     * 这样每个 handler 就是一个单例
     * 我们的 handler 是不涉及到状态的  所以 。。
     */
    public static void init() {
        //1.包掃描 拿當前包下所有實現了 ICmdHandler 接口的所有子類
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                CmdHandlerFactory.class.getPackage().getName(),
                true,
                ICmdHandler.class);

        log.info("packageScan clazzSet:{}",clazzSet);

        //2.遍歷
        for(Class<?> clazz: clazzSet){
            if( 0 != (clazz.getModifiers() & Modifier.ABSTRACT)){
              continue;// 過濾抽象類 接口
            }
            //3. 獲取方法數組
            Method[] methodArray = clazz.getDeclaredMethods();
            Class<?> msgType = null ;
            //4. 遍歷函數
            for (Method currMethod : methodArray) {
                if( !HANDLE.equals(currMethod.getName())){
                    continue;
                }
                //5. 拿到參數類型 0  1
                Class<?>[] parameterArray = currMethod.getParameterTypes();
                if(parameterArray.length != 2 ||// 不處理重載函數
                !GeneratedMessageV3.class.isAssignableFrom(parameterArray[1])){ // 消息類型不對直接過濾
                    continue;
                }

              msgType =  parameterArray[1];
              break;
            }

            if(null == msgType){
                continue; // 進入外層循環
            }

            // 6. 反射創建對象
            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.newInstance();

                log.info("{} <===> {}",msgType.getName(),clazz.getName());

                //7. 將我們的handler 加入 字典
                _handlerMap.put(msgType,newHandler);
            }catch (Exception ex){
                log.error(ex.getMessage(),ex);
            }
        }
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
