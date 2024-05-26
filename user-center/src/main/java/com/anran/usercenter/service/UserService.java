package com.anran.usercenter.service;

import com.anran.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* 用户登录
* @author anran
*
*/
public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param planetCode 星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode);


    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request 用户请求
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 原生对象
     * @return 脱敏后对象
     */
    User getSaftyUser(User originUser);

    /**
     * 用户注销
     * @param request 登录态和请求相关
     */
    int userLogout(HttpServletRequest request);
}
