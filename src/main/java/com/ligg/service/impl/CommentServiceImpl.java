package com.ligg.service.impl;

import com.ligg.mapper.CommentMapper;
import com.ligg.pojo.Comment;
import com.ligg.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public Comment addComment(String content, Integer videoId, Integer userId) {
        // 创建评论对象
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        comment.setCreateTime(new Date());
        comment.setLikes(0);
        
        // 保存评论
        commentMapper.addComment(comment);
        
        // 返回带ID的评论对象
        return comment;
    }
    
    @Override
    public List<Comment> getCommentsByVideoId(Integer videoId) {
        return commentMapper.getCommentsByVideoId(videoId);
    }
    
    @Override
    public int countCommentsByVideoId(Integer videoId) {
        return commentMapper.countCommentsByVideoId(videoId);
    }
    
    @Override
    public boolean deleteComment(Integer id, Integer userId) {
        int result = commentMapper.deleteComment(id, userId);
        return result > 0;
    }
    
    @Override
    public boolean incrementLikes(Integer id) {
        try {
            commentMapper.incrementLikes(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 