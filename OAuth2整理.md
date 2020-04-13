# OAuth2

## 1. OAuth2 介绍

​		**OAuth 2.0**是一种授权机制，用来授权第三方应用，获取用户数据。如第三方网站使用QQ、微信快捷登录。

#### 1.1 Spring Security

​		使用OAuth2.0之前需要了解Spring Security的基本使用。

​		Spring Security 是 Spring 家族中的一个安全管理框架，实际上，在 Spring Boot 出现之前，Spring Security 就已经出现了，但是使用的并不多，一直都是 Shiro居多 。

​		因为相比较Shiro，在SSM/SSH框架中整合Spring Security比较麻烦。虽然Spring Security功能比Shiro强大，但是使用却没有Shiro多。Spring Boot 出现之后，Spring Boot 对于 Spring Security 提供了 自动化配置方案，可以零配置使用 Spring Security。

​		一般来说，常见的安全管理技术栈的组合：

- SSM + Shiro

- Spring Boot/Spring Cloud + Spring Security

  实际使用中，任意组合都是可以的。

##### 1.1.1 Spring Security简单使用

​		使用Spring Boot框架。

1. Maven依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   ```

   添加依赖后，默认情况下项目中所有接口都会被保护起来。

2. 创建Controller

   ```java
   @RestController
   public class HelloController {
       @GetMapping("/test")
       public String hello() {
           return "Spring Security!";
       }
   }
   ```

   Spring Security支持form表单和HttpBasic两种认证方式。

   1. 浏览器直接访问 `/test` ，显示需要登录。

   ![image-20200413111916623](C:\Users\CQ\AppData\Roaming\Typora\typora-user-images\image-20200413111916623.png)

   ​		访问`/test`时，服务端会返回`302`响应码，客户端重定向到`/login`页面，用户在登录页面登录成功后，会重新跳转回`/test`接口。

   2. `POSTMAN`使用：

      请求头中添加Authorization认证，避免重定向到登录页面。

      ![image-20200413130947340](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200413130947340.png)      

      ​    


##### 1.1.2 用户名配置

​		默认情况下，登陆的用户名是`user`，密码在项目启动时随机生成，在控制台日志中查看。

![image-20200413131115683](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200413131115683.png)

​		随机生成的密码每次都会变更，实际开发中需要我们配置用户名密码。

  1. 在application.yaml中配置

     ```yaml
     spring: 
       security:
         user:
           name: user
           password: 123456
     ```

  2. 通过Java代码配置（数据库中加载、储存在内存中）

     创建一个 Spring Security 的配置类，继承`WebSecurityConfigurerAdapter`类

     ```java
     @Configuration
         public class SecurityConfig extends WebSecurityConfigurerAdapter {
             @Override
             protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                 //下面这两行配置表示在内存中配置了两个用户
                 auth.inMemoryAuthentication()
                     .withUser("admin").roles("admin")
                   .password("$2a$10$GStfEJEyoSHiSxnoP3SbD.R8XRowP1QKOdi.N6/iFEwEJWTQqlSba")
                         .and()
                     .withUser("user").roles("user")
                     .password("$2a$10$GStfEJEyoSHiSxnoP3SbD.R8XRowP1QKOdi.N6/iFEwEJWTQqlSba");
             }
             @Bean
             PasswordEncoder passwordEncoder() {
                 return new BCryptPasswordEncoder();
             }
         }
     ```

     ​		这样就配置了两个用户，密码是123456加密后的字符串。Spring5以后强制要求加密，可以使用过期的PasswordEncoder 的实例 NoOpPasswordEncoder来避免密码加密，但是一般情况下都会要求密码加密，即使用BCryptPasswordEncoder加密。

##### 1.1.3 登录配置



## OAuth2 授权服务器

## OAuth2 资源服务器

## OAuth2 完整案例