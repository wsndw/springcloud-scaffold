/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.access.control.system.server.service;

import cn.cq.springcloud.access.control.system.api.AccessProviderService;

/**
 * @ClassName AccessProviderServiceImpl
 * (功能描述)
 * 门禁系统测试接口实现
 * @Author cq
 * @Create 2020/4/3 10:10
 * @Version 1.0.0
 */
@org.apache.dubbo.config.annotation.Service
public class AccessProviderServiceImpl implements AccessProviderService {
    public String service() {
        return "Access Control System!";
    }
}
