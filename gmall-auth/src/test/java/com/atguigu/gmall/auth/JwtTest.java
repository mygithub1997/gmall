package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
	private static final String pubKeyPath = "D:\\IDEA_PROJECT\\secret\\rsa.pub";

    private static final String priKeyPath = "D:\\IDEA_PROJECT\\secret\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 2);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1ODM1ODQ5NTJ9.J4IGabpQjY_UKluaeLnQ_3ckpZ44W67FkVuy4wzquC-Ze0uTS22wxV1afy-5M5LxSVmee19cjpJezGgBxWtUnARWePGhSVmHHjh8a3hXPiQfOFFd6pPl0L4tWqhKW4LLlXQhtO_mDR7Ge24iLDbaV5whg4yIJhygqII7IPh_mjRvYLBMYoBYWJZmCv3I59y3M1dY8SN7KI1wVYdtpatl9nm6vPhrvw5p8YbOHWNBIuoqEITIa-WGenchjObMuXco6pb148_7hqwcTD32dVUh7npLfY4LyD78Sw6dtwwamg0bwjHbaY2rdRhnLQlvXRtoql6puixoysXqCr-J1wvgBA";
        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}