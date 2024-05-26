package com.anran.usercenter.service.impl;


import com.anran.usercenter.common.ErrorCode;
import com.anran.usercenter.exception.BusinessException;
import com.anran.usercenter.mapper.UserMapper;
import com.anran.usercenter.model.domain.User;
import com.anran.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.anran.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 * @author anran
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆加密
     */
    public static final String SALT = "anran";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
//        if(userAccount == null || userPassword == null || checkPassword == null){}
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)){
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过长");
        }
        if(userPassword.length() < 8 || checkPassword.length()  < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if(planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？%+_]";
        Matcher match = Pattern.compile(validPattern).matcher(userAccount);
        if(match.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        // 密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不相同");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已经存在");
        }
        // 星球编号不能重复 因为不频繁，与上面分开
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号重复");
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);  // 使用service层的逻辑
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存用户异常");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能小于4位");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能小于4位");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？%+_]";
        Matcher match = Pattern.compile(validPattern).matcher(userAccount);
        if(match.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if(user == null) {
            log.info("User login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 3. 用户脱敏
        User saftyUser = getSaftyUser(user);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, saftyUser); //session为SDK,把session的Attribute当做一个map使用

        return saftyUser;
    }

    // 为了与Controller那一串Java8的代码，使得Login的脱敏一致

    /**
     * 用户脱敏 ：这个脱敏就是将不敏感的信息返回了，但是敏感的并没有返回
     * @param originUser 脱敏前用户
     * @return 返回脱敏后用户
     */
    @Override
    public User getSaftyUser(User originUser){
        if(originUser == null){ // 判空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "脱敏前用户不存在");
        }
        User saftyUser = new User();
        saftyUser.setId(originUser.getId());
        saftyUser.setUsername(originUser.getUsername());
        saftyUser.setUserAccount(originUser.getUserAccount());
        saftyUser.setAvatarUrl(originUser.getAvatarUrl());
        saftyUser.setGender(originUser.getGender());
        saftyUser.setPhone(originUser.getPhone());
        saftyUser.setEmail(originUser.getEmail());
        saftyUser.setPlanetCode(originUser.getPlanetCode());
        saftyUser.setUserRole(originUser.getUserRole());
        saftyUser.setUserStatus(originUser.getUserStatus());
        saftyUser.setCreateTime(originUser.getCreateTime());

        return saftyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE); // 当做map去操作 移除登录态
        return 1;
    }
}