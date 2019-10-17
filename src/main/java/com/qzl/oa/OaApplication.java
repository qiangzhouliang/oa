package com.qzl.oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
/**
 * @author 强周亮
 * @desc 
 * @email 2538096489@qq.com
 * @time 2019/10/17 15:48
 * @class OaApplication
 * @package com.qzl.oa
 * SpringBootServletInitializer 继承这个可以支持jsp页面的调运
 */
@SpringBootApplication
public class OaApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application;
    }

    public static void main(String[] args) {
        SpringApplication.run(OaApplication.class, args);
    }

}
