package com.ligg.controller;

import com.ligg.pojo.Comment;
import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    /**
     * 添加评论
     */
    @PostMapping
    public Result<Comment> addComment(@RequestBody Map<String, Object> requestMap,
                                     HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 获取参数
        String content = (String) requestMap.get("content");
        Integer videoId = (Integer) requestMap.get("videoId");
        
        // 验证参数
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        
        if (videoId == null) {
            return Result.error("视频ID不能为空");
        }
        
        // 验证评论长度
        if (content.length() > 1000) {
            return Result.error("评论内容过长，请控制在1000字以内");
        }
        
        try {
            // 添加评论
            Comment comment = commentService.addComment(content, videoId, loginUser.getUserId());
            
            // 设置用户名
            comment.setUsername(loginUser.getUsername());
            
            return Result.success(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加评论失败");
        }
    }
    
    /**
     * 获取视频评论列表
     */
    @GetMapping("/video/{videoId}")
    public Result<List<Comment>> getCommentsByVideoId(@PathVariable Integer videoId) {
        try {
            List<Comment> comments = commentService.getCommentsByVideoId(videoId);
            return Result.success(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取评论失败");
        }
    }
    
    /**
     * 获取视频评论数量
     */
    @GetMapping("/count/{videoId}")
    public Result<Integer> countCommentsByVideoId(@PathVariable Integer videoId) {
        try {
            int count = commentService.countCommentsByVideoId(videoId);
            return Result.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取评论数量失败");
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Integer id, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        
        try {
            boolean success = commentService.deleteComment(id, loginUser.getUserId());
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，评论不存在或不属于当前用户");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除评论失败");
        }
    }
    
    /**
     * 评论点赞
     */
    @PostMapping("/like/{id}")
    public Result<String> likeComment(@PathVariable Integer id) {
        try {
            boolean success = commentService.incrementLikes(id);
            if (success) {
                return Result.success("点赞成功");
            } else {
                return Result.error("点赞失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("点赞失败");
        }
    }
} 