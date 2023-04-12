package org.ty.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理用户行为
 * final 修饰是不需要继承的
 */
public final class UserManager {

    //用户字典
    private static final Map<Integer, User> _userMap = new HashMap<>();

    /**
     * 私有构造
     */
    private UserManager(){}

    /**
     * 添加用户
     * @param newUser
     */
    public static void addUser(User newUser){
        if(null == newUser){
            return;
        }
        _userMap.put(newUser.getUserId(), newUser);
    }

    /**
     * 移除用户
     * @param userId
     */
    public static void removeUserById(Integer userId){
        if(null == userId){
            return;
        }
        _userMap.remove(userId);
    }

    /**
     * 获取所有用户
     * @return
     */
    public static Collection<User> listUser(){
        return _userMap.values();
    }

    /**
     * 根据id获取用户
     * @param userId
     * @return
     */
    public static User getUserById(int userId){
        return _userMap.get(userId);
    }
}
