/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.basic.system.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * (功能描述)
 * 基础系统测试接口
 * @Author cq
 * @Create 2020/4/2 8:53
 * @Version 1.0.0
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String testController(){
        return "Test!";
    }
}
