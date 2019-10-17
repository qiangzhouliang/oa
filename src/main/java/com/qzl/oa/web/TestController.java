package com.qzl.oa.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName TestController
 * @Description
 * @Author qzl
 * @Date 2019/10/17 15:43
 **/
@Controller
public class TestController {
    @GetMapping("hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @GetMapping("/")
    public String index(){
        return "login";
    }
}
