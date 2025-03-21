package com.ligg.service;

import com.ligg.pojo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    
    /**
     * 保存视频信息
     */
    Video saveVideo(String title, String description, MultipartFile videoFile, MultipartFile coverFile, Integer userId);
    
    /**
     * 获取用户上传的视频列表
     */
    List<Video> getUserVideos(Integer userId);
    
    /**
     * 获取所有视频列表
     */
    List<Video> getAllVideos();
    
    /**
     * 根据ID获取视频信息
     */
    Video getVideoById(Integer id);
    
    /**
     * 删除视频
     */
    boolean deleteVideo(Integer id, Integer userId);
    
    /**
     * 更新视频信息
     */
    boolean updateVideo(Video video);
    
    /**
     * 更新视频封面
     */
    boolean updateVideoCover(Integer videoId, MultipartFile coverFile, Integer userId);
    
    /**
     * 获取视频上传路径
     */
    String getUploadPath();
    
    /**
     * 获取封面上传路径
     */
    String getCoverUploadPath();
} 