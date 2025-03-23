package com.ligg.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;         // 视频ID
    private String title;       // 视频标题
    private String description; // 视频描述
    private String filePath;    // 视频文件路径
    private String coverUrl;    // 视频封面URL(过时的字段，保持兼容)
    private String videoImg;    // 视频封面图片路径(新字段)
    private String category;    // 视频分类
    private Integer userId;     // 上传用户ID
    private Integer viewCount;  // 观看次数
    private Date createTime;    // 创建时间
    private Date updateTime;    // 更新时间
} 