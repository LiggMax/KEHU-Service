package com.ligg.service;

import com.ligg.pojo.Comment;
import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 添加评论
     * @param content 评论内容
     * @param videoId 视频ID
     * @param userId 用户ID
     * @return 添加的评论对象
     */
    Comment addComment(String content, Integer videoId, Integer userId);
    
    /**
     * 获取视频评论列表
     * @param videoId 视频ID
     * @return 评论列表
     */
    List<Comment> getCommentsByVideoId(Integer videoId);
    
    /**
     * 获取视频评论数量
     * @param videoId 视频ID
     * @return 评论数量
     */
    int countCommentsByVideoId(Integer videoId);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteComment(Integer id, Integer userId);
    
    /**
     * 给评论点赞
     * @param id 评论ID
     * @return 是否点赞成功
     */
    boolean incrementLikes(Integer id);
} 