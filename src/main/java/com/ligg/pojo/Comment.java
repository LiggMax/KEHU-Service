package com.ligg.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 评论实体类
 */
@Data
public class Comment {
    /**
     * 评论ID
     */
    private Integer id;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 视频ID
     */
    private Integer videoId;
    
    /**
     * 评论用户ID
     */
    private Integer userId;
    
    /**
     * 评论时间
     */
    private Date createTime;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 评论用户名(非数据库字段)
     */
    private String username;

    private String videoTitle;
} 