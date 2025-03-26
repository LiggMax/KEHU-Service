package com.ligg.service.admin;

import com.ligg.pojo.Admin;

public interface AdminService {

    Admin login(String username, String password);
}