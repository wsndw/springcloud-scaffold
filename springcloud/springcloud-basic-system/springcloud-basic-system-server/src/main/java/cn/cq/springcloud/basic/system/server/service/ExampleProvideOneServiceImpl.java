/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.basic.system.server.service;

import cn.cq.springcloud.basic.system.api.ExampleProvideOneService;

/**
 * @ClassName ExampleProvideOneServiceImpl
 * (功能描述)
 * API接口实现
 * @Author cq
 * @Create 2020/3/31 14:50
 * @Version 1.0.0
 */
@org.apache.dubbo.config.annotation.Service
public class ExampleProvideOneServiceImpl implements ExampleProvideOneService {
    public String service() {
        return "Basic System!";
    }
}
