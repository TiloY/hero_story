package org.ty.herostory.login.db;

import org.apache.ibatis.annotations.Param;

public interface IUserDao {

    /**
     * 根据用户名称获取用户
     * @param userName
     * @return
     */
    UserEntity getUserByName(@Param("userName") String userName);

    /**
     * 添加用户
     * @param newUserEntity
     */
    void insertInto(UserEntity newUserEntity);
}
