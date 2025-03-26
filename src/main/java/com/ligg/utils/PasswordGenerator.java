package com.ligg.utils;

import org.springframework.util.DigestUtils;

public class PasswordGenerator {
    public static void main(String[] args) {
        String rawPassword = "admin123";
        String encodedPassword = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        System.out.println("加密后的密码: " + encodedPassword);
    }
} 