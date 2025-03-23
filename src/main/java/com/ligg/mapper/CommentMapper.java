package com.ligg.mapper;

import com.ligg.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评论数据访问层
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 添加评论
     */
    @Insert("INSERT INTO comments (content, video_id, user_id, create_time, likes) " +
            "VALUES (#{content}, #{videoId}, #{userId}, #{createTime}, #{likes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addComment(Comment comment);
    
    /**
     * 获取视频评论列表
     */
    @Select("SELECT c.*, u.username FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.user_id " +
            "WHERE c.video_id = #{videoId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> getCommentsByVideoId(Integer videoId);
    
    /**
     * 获取视频评论数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE video_id = #{videoId}")
    int countCommentsByVideoId(Integer videoId);
    
    /**
     * 删除评论
     */
    @Delete("DELETE FROM comments WHERE id = #{id} AND user_id = #{userId}")
    int deleteComment(Integer id, Integer userId);
    
    /**
     * 增加评论点赞数
     */
    @Update("UPDATE comments SET likes = likes + 1 WHERE id = #{id}")
    void incrementLikes(Integer id);
} 