# OAuth2

## 一. OAuth2 介绍

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

   SpringBoot可以使用

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   ```

   SpringCloud可以使用

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-security</artifactId>
   </dependency>
   ```

   添加依赖后，默认情况下项目中所有接口都会被保护起来（如果用户自定义了`WebSecurityConfigurerAdapter`类则会按照自定义规则）。

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

     ​		注意：
     
       1. 此处需要注入`@Bean` BCryptPasswordEncoder，不然控制台会报错，无法注入BCryptPasswordEncoder类。
     
       2. 配置多用户时，需要加入`and()`分隔。
     
          关于`and()`
     
          ```
             在没有 Spring Boot 的时候，我们都是 SSM 中使用 Spring Security，这种时候都是在 XML 文件中配置 Spring Security，既然是 XML 文件，标签就有开始有结束，现在的 and 符号相当于就是 XML 标签的结束符，表示结束当前标签，这是个时候上下文会回到 inMemoryAuthentication 方法中，然后开启新用户的配置。
          ```
     
     ​		这样就配置了两个用户，密码是123456加密后的字符串。Spring5以后强制要求加密，可以使用过期的PasswordEncoder 的实例 NoOpPasswordEncoder来避免密码加密，但是一般情况下都会要求密码加密，即使用BCryptPasswordEncoder加密。

##### 1.1.3 登录配置

​		对于登录接口，登录成功后的响应，登录失败后的响应，我们都可以在 WebSecurityConfigurerAdapter 的实现类中进行配置。

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    VerifyCodeFilter verifyCodeFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(verifyCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http
         	//开启登录配置
            .authorizeRequests()
            //表示访问 /hello 这个接口，需要具备 admin 这个角色
        	.antMatchers("/test").hasRole("admin")
            //表示剩余的其他接口，登录之后就能访问
        	.anyRequest().authenticated()
        	.and()
        	.formLogin()
            //自定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
            .loginPage("/login.html")
            //登录处理接口
            .loginProcessingUrl("/doLogin")
            //定义登录时，用户名的 key，默认为 username
            .usernameParameter("uname")
            //定义登录时，用户密码的 key，默认为 password
            .passwordParameter("passwd")
            //登录成功的处理器
            .successHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("success");
                    out.flush();
                }
            })
            //登录失败的处理器
            .failureHandler(new AuthenticationFailureHandler() {
                @Override
                public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("fail");
                    out.flush();
                }
            })
            .permitAll()//和表单登录相关的接口统统都直接通过
            .and()
            .logout()
            .logoutUrl("/logout")
            //登出成功的处理器
            .logoutSuccessHandler(new LogoutSuccessHandler() {
                @Override
                public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("logout success");
                    out.flush();
                }
            })
            .permitAll()
            .and()
            .httpBasic()
            .and()
            //关闭csrf跨域拦截
            .csrf().disable();
    }
}
```

如果某一个请求地址不需要拦截的话，可以配置忽略

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui");
    }
}
```

#### 1.2 OAuth2 角色

OAuth 2.0 中有四种类型的角色分别为：`资源Owner`、`授权服务`、`客户端`、`资源服务`

​					授权流程

![image-20200414165529651](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414165529651.png)

**资源 Owner (资源所有者)**

​		资源 Owner（拥有者）可以理解为一个用户。

​		如使用QQ登录网易云音乐，用户使用QQ账号登录网易云音乐，网易就需要知道用户在QQ中的的头像、用户名等信息，这些账户信息都是属于用户的。

​		这样就是资源 Owner了。在网易请求从QQ中获取想要的用户信息时，QQ为了安全起见，需要通过用户（资源 Owner）的同意。

**资源服务器**

​		资源服务器存放受保护资源，要访问这些资源，需要获得访问令牌

​		用户账号的信息都存放在QQ的服务器中，所以这里的资源服务器就是QQ服务器。QQ服务器负责保存、保护用户的资源，任何其他第三方系统想到使用这些信息的系统都需要经过资源 Owner授权，同时依照 OAuth 2.0 授权流程进行交互。

**客户端**

​		客户端代表请求资源服务器资源的第三方程序，**客户端同时也可能是一个资源服务器**

​		**客户端就是想要获取资源的系统**，如使用QQ登录网易时，网易就是OAuth中的客户端。客户端主要负责发起授权请求、获取AccessToken、获取用户资源。

**授权服务器**

​		授权服务器用于发放访问令牌给客户端

​		有了资源 Owner、资源服务器、客户端还不能完成OAuth授权的，还需要有授权服务器。在OAuth中授权服务器除了负责与用户（资源 Owner）、客户端（网易）交互外，还要生成AccessToken、验证AccessToken等功能，它是OAuth授权中的非常重要的一环，在例子中授权服务器就是GitHub的服务器。

#### 1.3 授权类型

​		客户端必须得到用户的授权（authorization grant），才能获得令牌（access token）。OAuth 2.0定义了四种授权方式。

- 授权码模式（authorization code）
- 简化模式（implicit）
- 密码模式（resource owner password credentials）
- 客户端模式（client credentials）

##### 1.3.1 授权码授权

​		授权码模式是功能最完整、流程最严密的授权模式，它的特点是通过客户端的后台服务器，与“服务器提供”的认证服务器进行互动。

![image-20200414184813817](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414184813817.png)

​			A. 用户访问客户端，后者将前者导向认证服务器。

​			B. 用户选择是否给予客户端授权。

​			C. 假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。

​			D. 客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。

​			E. 认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）。

##### 1.3.2 用户的密码授权

​		密码模式（Resource Owner Password Credentials Grant）中，用户向客户端提供自己的用户名和密码。客户端使用这些信息，向"服务商提供商"索要授权。

​		在这种模式中，用户必须把自己的密码给客户端，但是客户端不得储存密码。这通常用在用户对客户端高度信任的情况下，比如客户端是操作系统的一部分，或者由一个著名公司出品。而认证服务器只有在其他授权模式无法执行的情况下，才能考虑使用这种模式。

![image-20200414185059002](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414185059002.png)

​			A. 用户向客户端提供用户名和密码。

​			B. 客户端将用户名和密码发给认证服务器，向后者请求令牌。

​			C. 认证服务器确认无误后，向客户端提供访问令牌。

##### 1.3.3 客户端凭证授权

​		客户端模式（Client Credentials Grant）指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。严格地说，客户端模式并不属于OAuth框架所要解决的问题。在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在授权问题。

![image-20200414185220993](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414185220993.png)

​			A. 客户端向认证服务器进行身份认证，并要求一个访问令牌。

​			B. 认证服务器确认无误后，向客户端提供访问令牌。

##### 1.3.4 简化授权

​		简化模式（implicit grant type）不通过第三方应用程序的服务器，直接在浏览器中向认证服务器申请令牌，跳过了"授权码"这个步骤，因此得名。所有步骤在浏览器中完成，令牌对访问者是可见的，且客户端不需要认证。

![image-20200414185323137](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414185323137.png)

​				A. 客户端将用户导向认证服务器。

​				B. 用户决定是否给于客户端授权。

​				C. 假设用户给予授权，认证服务器将用户导向客户端指定的"重定向URI"，并在URI的Hash部分包含了访问令牌。

​				D. 浏览器向资源服务器发出请求，其中不包括上一步收到的Hash值。

​				E. 资源服务器返回一个网页，其中包含的代码可以获取Hash值中的令牌。

​				F. 浏览器执行上一步获得的脚本，提取出令牌。

​				G. 浏览器将令牌发给客户端。



​		不同的授权类型可以使用在不同的场景中。

#### 1.4 更新令牌

​		如果用户访问的时候，客户端的"访问令牌"已经过期，则需要使用"更新令牌"申请一个新的访问令牌。

​		客户端发出更新令牌的HTTP请求，包含以下参数：

- grant_type：表示使用的授权模式，此处的值固定为"refresh_token"，必选项。
- refresh_token：表示早前收到的更新令牌，必选项。
- scope：表示申请的授权范围，不可以超出上一次申请的范围，如果省略该参数，则表示与上一次一致。

![image-20200414185632367](C:\Users\CQ\Documents\OAuth2整理.assets\image-20200414185632367.png)

## 二、OAuth2 授权服务器

#### 2.1



自定义令牌









































## 三、OAuth2 资源服务器























































## 四、OAuth2 完整案例