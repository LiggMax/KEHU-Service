package com.ligg.service.admin.impl;

import com.ligg.mapper.AdminMapper;
import com.ligg.pojo.Admin;
import com.ligg.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public Admin login(String username, String password) {

        Admin admin = adminMapper.finByAdminUser(username,password);
        return admin;
    }
}
