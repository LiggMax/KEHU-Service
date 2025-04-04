package com.ligg.mapper;

import com.ligg.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
    int update(User user);

    /**
     * 获取所有用户列表
     */
    @Select("select * from users")
    List<User> getAllUsers();

    /**
     * 根据用户名模糊搜索用户
     */
    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{username}, '%')")
    List<User> searchUsersByUsername(String username);
}
