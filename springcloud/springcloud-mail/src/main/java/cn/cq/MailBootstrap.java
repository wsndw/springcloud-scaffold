package cn.cq;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/***
 * 邮件告警
 * @author CQ
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2Doc
public class MailBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MailBootstrap.class,args);
    }

}
