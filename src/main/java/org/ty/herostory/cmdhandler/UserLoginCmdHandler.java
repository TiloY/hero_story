package org.ty.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.login.LoginService;
import org.ty.herostory.login.db.UserEntity;
import org.ty.herostory.model.User;
import org.ty.herostory.model.UserManager;
import org.ty.herostory.msg.GameMsgProtocol;

/**
 * 处理 登录消息
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志对戏
     */
    private final static Logger log = LoggerFactory.getLogger(UserLoginCmdHandler.class);


    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {

        log.info(
                "userName = {}, password = {}",
                cmd.getUserName(),
                cmd.getPassword()
        );

        UserEntity userEntity = null;
        try {
            userEntity = LoginService.getInstance().userLogin(
                    cmd.getUserName(),
                    cmd.getPassword()
            );
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        if (null == userEntity) {
            log.error("用户登录失败, userName = {} ", cmd.getUserName());
            return;
        }

        int userId = userEntity.getUserId();
        String heroAvatar = userEntity.getHeroAvatar();

        //新建用户
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserName(userEntity.getUserName());
        newUser.setHeroAvatar(heroAvatar);
        newUser.setCurrHp(100f);
        UserManager.addUser(newUser);

        //将userId 和 channel 绑定
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        GameMsgProtocol.UserLoginResult.Builder
                resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
        resultBuilder.setUserId(newUser.getUserId());
        resultBuilder.setUserName(newUser.getUserName());
        resultBuilder.setHeroAvatar(newUser.getHeroAvatar());

        //构建结果并发送
        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
