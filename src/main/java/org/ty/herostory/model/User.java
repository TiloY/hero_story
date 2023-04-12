package org.ty.herostory.model;

/**
 * 用户
 */
public class User {
    //用户id
    private Integer userId ;
    //用户形象
    private String heroAvatar ;

    private final MoveState moveState = new MoveState();

    public User() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public MoveState getMoveState() {
        return moveState;
    }
}
