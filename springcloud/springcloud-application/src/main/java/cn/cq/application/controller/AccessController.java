/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.application.controller;

import cn.cq.springcloud.access.control.system.api.AccessProviderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AccessController
 * (功能描述)
 * 门禁dubbo接口调用测试接口
 * @Author cq
 * @Create 2020/4/3 10:19
 * @Version 1.0.0
 */
@RestController
public class AccessController {

    @org.apache.dubbo.config.annotation.Reference
    AccessProviderService accessProviderService;

    @RequestMapping("/accessProviderService")
    public String accessProviderService(){
        return "Example!" + accessProviderService.service();
    }
}
