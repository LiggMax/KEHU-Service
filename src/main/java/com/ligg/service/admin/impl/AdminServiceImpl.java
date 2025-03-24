package com.ligg.service.admin.impl;

import com.ligg.mapper.admin.AdminMapper;
import com.ligg.pojo.Admin;
import com.ligg.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(String username, String password) {

        // 查询管理员
        return adminMapper.login(username, password);
    }

    @Override
    public void updateLastLoginInfo(Long adminId, String ip) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        
        adminMapper.updateLastLoginInfo(admin);
    }

    @Override
    public Admin getById(Long id) {
        return adminMapper.getById(id);
    }
} 