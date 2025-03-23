package com.ligg.mapper;

import com.ligg.pojo.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {
    
    /**
     * 保存视频信息
     */
    @Insert("INSERT INTO videos (title, description, file_path, cover_url, video_img, category, user_id, view_count, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{filePath}, #{coverUrl}, #{videoImg}, #{category}, #{userId}, #{viewCount}, #{createTime}, #{updateTime})")
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
    @Update("UPDATE videos SET title = #{title}, description = #{description}, category = #{category}, video_img = #{videoImg}, update_time = #{updateTime} WHERE id = #{id}")
    int updateVideo(Video video);
    
    /**
     * 增加观看次数
     */
    @Update("UPDATE videos SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);
    
    /**
     * 搜索视频（模糊查询）
     * @param keyword 搜索关键词
     * @return 匹配的视频列表
     */
    @Select("SELECT * FROM videos WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<Video> searchVideos(String keyword);
    
    /**
     * 分页搜索视频
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 匹配的视频列表
     */
    @Select("SELECT * FROM videos WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Video> searchVideosPaged(String keyword, int offset, int limit);
    
    /**
     * 统计搜索结果数量
     * @param keyword 搜索关键词
     * @return 匹配的视频数量
     */
    @Select("SELECT COUNT(*) FROM videos WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchResults(String keyword);
    
    /**
     * 获取所有分类
     * @return 所有不重复的分类
     */
    @Select("SELECT DISTINCT category FROM videos WHERE category IS NOT NULL AND category != '' ORDER BY category")
    List<String> getAllCategories();
    
    /**
     * 根据分类获取视频
     * @param category 分类名称
     * @return 该分类下的视频列表
     */
    @Select("SELECT * FROM videos WHERE category = #{category} ORDER BY create_time DESC")
    List<Video> getVideosByCategory(String category);
    
    /**
     * 分页获取分类视频
     * @param category 分类名称
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 该分类下的视频列表（带分页）
     */
    @Select("SELECT * FROM videos WHERE category = #{category} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Video> getVideosByCategoryPaged(String category, int offset, int limit);
    
    /**
     * 统计分类下的视频数量
     * @param category 分类名称
     * @return 该分类下的视频数量
     */
    @Select("SELECT COUNT(*) FROM videos WHERE category = #{category}")
    int countVideosByCategory(String category);
    
    /**
     * 获取所有分类及其视频数量
     * @return 分类和对应的视频数量
     */
    @Select("SELECT category, COUNT(*) as count FROM videos WHERE category IS NOT NULL AND category != '' GROUP BY category ORDER BY category")
    @Results({
        @Result(property = "key", column = "category"),
        @Result(property = "value", column = "count")
    })
    List<Map<String, Object>> getCategoriesWithCount();
    
    /**
     * 获取搜索相关的视频作者
     * @param keyword 搜索关键词
     * @return 相关作者列表
     */
    @Select("SELECT DISTINCT u.username FROM videos v " +
            "JOIN users u ON v.user_id = u.user_id " +
            "WHERE v.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR v.description LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT 10")
    List<String> getRelatedAuthors(String keyword);
    
    /**
     * 获取热门搜索关键词（基于最近的搜索记录）
     * 注意：实际实现可能需要一个搜索历史表
     * @return 热门搜索关键词列表
     */
    @Select("SELECT keyword FROM search_history " +
            "GROUP BY keyword " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 10")
    List<String> getHotSearchKeywords();
    
    /**
     * 保存搜索记录
     * @param keyword 搜索关键词
     * @param userId 用户ID，可以为null表示未登录用户
     */
    @Insert("INSERT INTO search_history (keyword, user_id, search_time) " +
            "VALUES (#{keyword}, #{userId}, NOW())")
    void saveSearchHistory(String keyword, Integer userId);
} 