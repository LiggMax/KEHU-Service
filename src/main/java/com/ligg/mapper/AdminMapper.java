package com.ligg.mapper;

import com.ligg.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admins where username =#{username} and password =#{password} ")
    Admin finByAdminUser(String username, String password);

}
