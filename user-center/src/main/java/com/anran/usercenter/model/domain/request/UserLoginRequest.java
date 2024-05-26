package com.anran.usercenter.model.domain.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求体
 * @author anran
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3171241716373120793L;

    private String userAccount;
    private String userPassword;
}
