/**
 * Copyright (C), 2018-2020, cq
 * History:
 * <author>    <create>    <version>   <desc>
 * 作者姓名     修改时间        版本号    功能描述
 */
package cn.cq.springcloud.gateway;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GatewayBootstrap
 * (功能描述)
 * 网关启动类
 * @Author cq
 * @Create 2020/3/31 15:45
 * @Version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableSwagger2Doc
public class GatewayBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(GatewayBootstrap.class,args);
    }

    @Component
    @Primary
    class DocumentationConfig implements SwaggerResourcesProvider {
        @Override
        public List<SwaggerResource> get() {
            List resources = new ArrayList<>();
            resources.add(swaggerResource("springcloud-basic-system", "/springcloud-basic-system/v2/api-docs", "2.0"));
            resources.add(swaggerResource("springcloud-application", "/springcloud-application/v2/api-docs", "2.0"));
            resources.add(swaggerResource("springcloud-access-control-system", "/springcloud-access-control-system/v2/api-docs", "2.0"));
            return resources;
        }
        //定义swaggerResource的构造函数
        private SwaggerResource swaggerResource(String name, String location, String version) {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(name);
            swaggerResource.setLocation(location);
            swaggerResource.setSwaggerVersion(version);
            return swaggerResource;
        }
    }
}
