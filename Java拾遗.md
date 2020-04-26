### **1.postgresql返回主键自增**

```xml
<insert id="insertByUser" parameterType="com.precision.common.entity.basicandauth.MyUser">
        <selectKey resultType="java.lang.Integer" order="AFTER" keyProperty="id">
            SELECT currval('basic_user_id_seq')
        </selectKey>
        insert into public.basic_user (user_name, pass_word,
        sex, img_url, birthday,
        phone, email, status,
        create_time, update_time)
        values (#{userName,jdbcType=VARCHAR}, #{passWord,jdbcType=VARCHAR},
        #{sex,jdbcType=SMALLINT}, #{imgUrl,jdbcType=VARCHAR}, #{birthday,jdbcType=DATE},
        #{phone,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR}, #{status,jdbcType=SMALLINT},
        #{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})
    </insert>
```

**注意**

postgre设置主键自增时，直接在创建表时，将字段设置为serial，会自动生成序列

使用`SELECT currval('basic_user_id_seq')`，不是`nextval('basic_user_id_seq'::regclass)`

不然主键会每次自增两个1 3 5 7 9





### **2.字段列表中的“id”列不明确**

一般出现在检查联合查询时，两张表同时有id字段，不能直接指定返回id，应该具体到那张表的那个id。

```xml
<sql id="Base_Column_List">
        p.id, parent_id, method, zuul_prefix, service_prefix, uri, name
    </sql>

    <select id="findByRoleId" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from public.basic_role_permission rp,public.basic_permission p
        where rp.permission_id = p.id and rp.role_id = #{roleId,jdbcType=INTEGER}
    </select>
```



### 3.不要在sql中写*

防止sql注入，不要在mapper映射sql时使用select *，应该使用resultMap指定

```xml
<resultMap id="BaseResultMap" type="com.precision.common.entity.basicandauth.Permission">
        <id column="id" jdbcType="INTEGER" property="id" />
        <result column="parent_id" jdbcType="INTEGER" property="parentId" />
        <result column="method" jdbcType="VARCHAR" property="method" />
        <result column="zuul_prefix" jdbcType="VARCHAR" property="zuulPrefix" />
        <result column="service_prefix" jdbcType="VARCHAR" property="servicePrefix" />
        <result column="uri" jdbcType="VARCHAR" property="uri" />
        <result column="name" jdbcType="VARCHAR" property="name" />
    </resultMap>
```





### 4.加盐密码比较

**BCryptPasswordEncoder 判断密码是否相同**

加密

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
encode.encode(password);
```

比较

```java
matches(CharSequence rawPassword, String encodedPassword)    
```



需要通过自带的方法 matches 将未经过加密的密码和已经过加密的密码传进去进行判断，返回布尔值。

举例

```java
public class BCryptPasswordEncoderTest {
    public static void main(String[] args) {
        String pass = "admin";
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPass = bcryptPasswordEncoder.encode(pass); 
        System.out.println(hashPass);
 
        boolean flag = bcryptPasswordEncoder.matches("admin",hashPass);
        System.out.println(flag); 
    }}
```

可以看到，每次输出的hashPass 都不一样，但是最终的flag都为 true,即匹配成功。

查看代码，可以看到，其实每次的随机盐，都保存在hashPass中。在进行matchs进行比较时，调用BCrypt 的String hashpw(String password, String salt)方法。两个参数即”admin“和 hashPass。

```java
//******BCrypt.java******salt即取出要比较的DB中的密码*******
real_salt = salt.substring(off + 3, off + 25);
try {
// ***************************************************
    passwordb = (password + (minor >= 'a' ? "\000" : "")).getBytes("UTF-8");
}
catch (UnsupportedEncodingException uee) {}
saltb = decode_base64(real_salt, BCRYPT_SALT_LEN);
B = new BCrypt();
hashed = B.crypt_raw(passwordb, saltb, rounds);
```

假定一次hashPass为：$2a$10$AxafsyVqK51p.s9WAEYWYeIY9TKEoG83LTEOSB3KUkoLtGsBKhCwe

随机盐即为 AxafsyVqK51p.s9WAEYWYe（salt = BCrypt.*gensalt*();中有描述），可见，随机盐（AxafsyVqK51p.s9WAEYWYe），会在比较的时候，重新被取出。

即，加密的hashPass中，前部分已经包含了盐信息。





### 5.