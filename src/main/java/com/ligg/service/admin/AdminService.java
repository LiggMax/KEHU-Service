package com.ligg.service.admin;

import com.ligg.pojo.Admin;

public interface AdminService {
    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回管理员信息，失败返回null
     */
    Admin login(String username, String password);

    /**
     * 更新管理员最后登录信息
     * @param adminId 管理员ID
     * @param ip 登录IP
     */
    void updateLastLoginInfo(Long adminId, String ip);

    /**
     * 根据ID获取管理员信息
     * @param id 管理员ID
     * @return 管理员信息
     */
    Admin getById(Long id);
} 