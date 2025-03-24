package com.ligg.mapper.admin;

import com.ligg.pojo.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    
    @Select("SELECT * FROM admin WHERE username = #{username} AND password = #{password} AND status = 1")
    Admin login(@Param("username") String username, @Param("password") String password);
    
    @Update("UPDATE admin SET last_login_time = #{lastLoginTime}, last_login_ip = #{lastLoginIp} WHERE id = #{id}")
    int updateLastLoginInfo(Admin admin);
    
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin getById(@Param("id") Long id);
} 