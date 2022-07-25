package com.ihrm.demo;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JSON Web Token（JWT）是一个非常轻巧的规范。这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息。在Java世界中通过JJWT实现JWT创建和验证。
 * (通过jjwt创建token，把我们的账户等信息放在token里面)
 */
public class CreateJwtTest {
    public static void main(String[] args) {
        JwtBuilder builder= Jwts.builder().setId("888")
                .setSubject("小白")
                .setIssuedAt(new Date())        //当前token创建的时间
                .signWith(SignatureAlgorithm.HS256,"hyf12321")  //设置签名(在网络传输中，可以保证我们的信息不被篡改)，hyf是私钥
                .claim("companyId","1") //设置自定义的内容，可以用于我们的项目
                .claim("companyName","美团有限公司");
        String token = builder.compact();
        System.out.println(token);
    }
}
