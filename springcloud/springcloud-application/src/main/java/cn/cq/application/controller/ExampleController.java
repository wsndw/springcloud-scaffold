/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.application.controller;

import cn.cq.springcloud.basic.system.api.ExampleProvideOneService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ExampleController
 * (功能描述)
 * 示例接口
 * @Author cq
 * @Create 2020/3/31 14:15
 * @Version 1.0.0
 */
@RestController
public class ExampleController {

    @org.apache.dubbo.config.annotation.Reference
    ExampleProvideOneService exampleProvideOneService;

    @RequestMapping("/exampleService")
    public String exampleService(){
        return "Example!" + exampleProvideOneService.service();
    }

}
