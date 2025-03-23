package com.ligg.mapper;

import com.ligg.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from users where username=#{username} and password=#{password}")
    User getUser(String username, String password);

    @Select("select * from users where username=#{username}")
    User getUserByUsername(String username);
    
    @Select("select * from users where user_id=#{userId}")
    User getUserById(Integer userId);
    
    @Insert("insert into users(username,password,nickname) " +
            "values(#{username},#{password},#{username})")
    void register(String username, String password);
    
    /**
     * 更新用户昵称
     */
    @Update("update users set nickname=#{nickname} where user_id=#{userId}")
    int updateNickname(Integer userId, String nickname);
    
    /**
     * 更新用户头像
     */
    @Update("update users set avatar=#{avatar} where user_id=#{userId}")
    int updateAvatar(Integer userId, String avatar);
    
    /**
     * 更新用户信息
     */
    @Update("update users set nickname=#{nickname}, avatar=#{avatar} where user_id=#{userId}")
    int updateUserInfo(User user);
}
