/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.basic.system.server;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName ApplicationBootstrap
 * (功能描述)
 * 应用服务启动类
 * @Author cq
 * @Create 2020/3/31 14:28
 * @Version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2Doc
public class BasicSystemBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(BasicSystemBootstrap.class,args);
    }
}
