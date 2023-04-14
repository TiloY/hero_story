package org.ty.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ty.herostory.MySqlSessionFactory;
import org.ty.herostory.login.db.IUserDao;
import org.ty.herostory.login.db.UserEntity;

/**
 * 登录服务
 */
public final class LoginService {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 私有化构造
     */
    private LoginService() {
    }

    public static LoginService getInstance(){
        return _instance ;
    }

    /**
     * 用户登录
     *
     * @param userName 用户名称
     * @param password 用户密码
     * @return
     */
    public UserEntity userLogin(String userName, String password) {
        if (null == userName
                || null == password) {
            return null;
        }

        log.info("当前线程= {} ",Thread.currentThread().getName());

        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
            //获取DAO
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);
            // 获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);

            if (null != userEntity) {
                if (!password.equals(userEntity.getPassword())) {
                    log.error(
                            "用户密码错误,userName = {} ",
                            userName
                    );
                    throw new RuntimeException("用户密码错误");
                }
            } else {
                userEntity = new UserEntity();
                userEntity.setUserName(userName);
                userEntity.setPassword(password);
                userEntity.setHeroAvatar("Hero_Shaman");

                //将用户实体插入到数据库
                dao.insertInto(userEntity);
            }

            return userEntity;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

}
