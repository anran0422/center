package com.anran.usercenter.service;
import java.util.Date;

import com.anran.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("anran-dog");
        user.setUserAccount("123");
        user.setAvatarUrl("https://www.baidu.com/link?url=E_MN4kEpvPxvmSCjHVSvK8SRPrmJuajBDePjFCWwkZKzU1lqmrBnG64y0gfmIAIn4vx4J4TwNNGB86PBRJx0VL1Tpk4mgQYVwOLLkTEqzHl-Xk2DEsWnP4c3t4Qm00JtJMW5tL67WhFLrkCVU1NpWOX6ZUvfPztw2B6rRe2nu2210Wxh3FvVrN-czt_VSTyFX0nbpnBpteXFTrT3X1-O3B6dgLOWZ5ZdI2Lar-NAH0ycTnQBorfIeJnHpAojDThcprMmKlSHQBrzpPOUjnIroCHkYGkNAbeG6-lOeRnAEwtrbLrdcG_ghx3CbxEk1AKHOp_YJF8NfUk7yoU5idTvCYoDbg_ON_Mejv1RhewlQ2FD8Fw7wwjF4PaYY054ulsmoQTuISHwH66AOKVn2_wD_0anT0kbSYq2zDidwsOae5g8CbnN_PG2jvikAhf5xC-GK4BxpRJKD1Q9NppPRckIaQjxNb6jZ860NMjiGeAkOdP1lhm1vr6DyCo_NriZxLL9dsq4lNYoak5HJgSPvZhxsILBNMPCHq9WCT4rT_o6nKQgclLHO2-BaoY75Y5XI-IyzKwiTX0w5ZfbtNc_W8xilKw41eLYAMEmIm-nJh3dlEYSP5fy76aAuTcL7khUor8L37t8AthKHhNCoFeWw4tEg8wGp1wkwrkNHlesI1TpjC9NTsCtd4NgWDVCr_BpRmTs&wd=&eqid=a4fa9aa600056c170000000365ec7b79");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("456");

        // 虽然没有去赋值ID，但是框架会自动帮我们把生成的id放到类对象里
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result); // 帮助测试
    }

    @Test
    void userRegister() {
        String userAccount = "";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "an";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "anran";
        userPassword = "123456";
        checkPassword = "123456";
        planetCode = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "an ran";
        userPassword = "123456";
        checkPassword = "123456";
        planetCode = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        userAccount = "anran";
        userPassword = "123456789";
        checkPassword = "123456789";
        planetCode = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertTrue(result > 0);
    }
}