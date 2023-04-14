package org.ty.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * MySql 会话工厂
 */
public final class MySqlSessionFactory {
    /**
     * Mybatis SQL 会话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有构造
     */
    private MySqlSessionFactory() {
    }

    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MyBatisConfig.xml"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 开启MySql 会话
     *
     * @return MySql 会话
     */
    public static SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
