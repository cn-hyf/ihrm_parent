package com.ihrm.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.swing.event.CaretListener;

/**
 * 1.解析token中字符串
 * 我们刚才在CreateJwtTest中已经创建了token ，在web应用中这个操作是由服务端进行然后发给客户端，客户端在下次向服务端发送请求时需要携带这个token（这就好像是拿着一张门票一样），
 * 那服务端接到这个token 应该解析出token中的信息（例如用户id）,根据这些信息查询数据库返回相应的结果。
 */
public class ParseJwtTest {
    public static void main(String[] args) {
        String token= "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE2NTg3NzQwMDcsImNvbXBhbnlJZCI6IjEiLCJjb21wYW55TmFtZSI6Iue-juWbouaciemZkOWFrOWPuCJ9.B-e3Hohgbjolg5vpadmAPNHKg3eTBix04s1lHdBLhSU";
        Claims claims = Jwts.parser().setSigningKey("hyf12321").parseClaimsJws(token).getBody(); //hyf12321是在创建token时设置的私钥。

        //私有数据存在claims
        System.out.println("id:"+claims.getId());
        System.out.println("subject:"+claims.getSubject());
        System.out.println("IssuedAt:"+claims.getIssuedAt());

        //解析自定义claim中的内容
        String companyId = (String) claims.get("companyId");
        String companyName = (String) claims.get("companyName");
        System.out.println("公司的id："+companyId);
        System.out.println("公司的名称："+companyName);
    }

}
