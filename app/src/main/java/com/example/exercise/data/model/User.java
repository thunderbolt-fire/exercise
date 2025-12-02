package com.example.exercise.data.model;

/**
 * 用户数据模型类 (POJO - Plain Old Java Object)。
 * <p>
 * 这个类用于封装用户的核心信息，如邮箱和密码。
 * 它的实例可以在应用的不同层之间传递，例如从数据库到UI。
 */
public class User {

    // 用户的邮箱地址
    private String email;
    // 用户的密码
    private String password;

    /**
     * 构造函数，用于创建一个新的User对象。
     *
     * @param email    用户的邮箱。
     * @param password 用户的密码。
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * 获取用户的邮箱。
     *
     * @return 用户的邮箱字符串。
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户的邮箱。
     *
     * @param email 新的邮箱地址。
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取用户的密码。
     *
     * @return 用户的密码字符串。
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户的密码。
     *
     * @param password 新的密码。
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
