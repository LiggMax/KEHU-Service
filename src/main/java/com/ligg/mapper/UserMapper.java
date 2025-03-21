package com.ligg.mapper;

import com.ligg.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from users where username=#{username} and password=#{password}")
    User getUser(String username, String password);

    @Select("select * from users where username=#{username}")
    User getUserByUsername(String username);
    
    @Insert("insert into users(username,password) " +
            "values(#{username},#{password})")
    void register(String username, String password);
}
