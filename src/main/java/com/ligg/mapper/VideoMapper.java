package com.ligg.mapper;

import com.ligg.pojo.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoMapper {
    
    /**
     * 保存视频信息
     */
    @Insert("INSERT INTO videos (title, description, file_path, cover_url, user_id, view_count, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{filePath}, #{coverUrl}, #{userId}, #{viewCount}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveVideo(Video video);
    
    /**
     * 根据用户ID获取视频列表
     */
    @Select("SELECT * FROM videos WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Video> getVideosByUserId(Integer userId);
    
    /**
     * 获取所有视频列表
     */
    @Select("SELECT * FROM videos ORDER BY create_time DESC")
    List<Video> getAllVideos();
    
    /**
     * 根据ID获取视频
     */
    @Select("SELECT * FROM videos WHERE id = #{id}")
    Video getVideoById(Integer id);
    
    /**
     * 删除视频
     */
    @Delete("DELETE FROM videos WHERE id = #{id} AND user_id = #{userId}")
    int deleteVideo(Integer id, Integer userId);
    
    /**
     * 更新视频信息
     */
    @Update("UPDATE videos SET title = #{title}, description = #{description}, update_time = #{updateTime} WHERE id = #{id}")
    int updateVideo(Video video);
    
    /**
     * 增加观看次数
     */
    @Update("UPDATE videos SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);
} 