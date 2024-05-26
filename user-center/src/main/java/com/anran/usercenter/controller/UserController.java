package com.anran.usercenter.controller;

import com.anran.usercenter.common.BaseResponse;
import com.anran.usercenter.common.ErrorCode;
import com.anran.usercenter.common.ResultUtils;
import com.anran.usercenter.exception.BusinessException;
import com.anran.usercenter.model.domain.User;
import com.anran.usercenter.model.domain.request.UserLoginRequest;
import com.anran.usercenter.model.domain.request.UserRegisterRequest;
import com.anran.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.anran.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.anran.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author anran
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = {"http://center.planetdream.chat"}, allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){ // 简单校验为空
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){ // 简单校验为空
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) { // session不
        if(request == null) {
            return null;
        }
        int id = userService.userLogout(request);
        return ResultUtils.success(id);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        // 这个只是获取到了一个用户的登录信息，如果用户的一些积分属性改变，可能获取不到，需要查询数据库
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        // 查询数据库
        Long userId = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(userId);
        User saftyUser = userService.getSaftyUser(user);
        return ResultUtils.success(saftyUser);
    }



    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR, "无权限访问");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName", userName);
        }
        // Java8的知识： List转化为数据流，遍历每一个元素，将密码设置为null，最后再拼成一个List
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSaftyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR, "无权限访问");
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        boolean removeById = userService.removeById(id);// 这个框架做到的就是逻辑删除
        return ResultUtils.success(removeById);
    }

    /**
     * 是否为管理员
     * @param request 请求
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObject;
        // 不是管理员返回空数组
        return user != null && user.getUserRole() == ADMIN_ROLE; // 帮助简写代码
    }
}
