/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.access.control.system.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestAccessProviderController
 * (功能描述)
 * 门禁系统测试接口
 * @Author cq
 * @Create 2020/4/3 10:13
 * @Version 1.0.0
 */
@RestController
public class TestAccessProviderController {

    @GetMapping("/Access")
    public String testController(){
        return "Test Access!";
    }
}