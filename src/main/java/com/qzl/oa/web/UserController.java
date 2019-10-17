package com.qzl.oa.web;

import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserController
 * @Description 登录功能
 * @Author qzl
 * @Date 2019/10/17 16:07
 **/
@Controller
public class UserController {
    //身份服务
    @Autowired
    private IdentityService identityService;
    @RequestMapping("/login")
    public String login(String name, String passwd, HttpSession session){
        //判断用户名和密码是否匹配
        boolean success = identityService.checkPassword(name,passwd);
        System.out.println(name+"-------"+passwd);
        if (success) {
            session.setAttribute("user",name);
            return "main";
        } else {
            return "login";
        }
    }
}
