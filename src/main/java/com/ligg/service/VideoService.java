package com.ligg.service;

import com.ligg.pojo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    
    /**
     * 保存视频文件并创建视频记录
     * @param title 视频标题
     * @param description 视频描述
     * @param videoFile 视频文件
     * @param userId 用户ID
     * @return 保存的视频对象
     */
    Video saveVideo(String title, String description, MultipartFile videoFile, Integer userId);
    
    /**
     * 获取用户的视频列表
     * @param userId 用户ID
     * @return 视频列表
     */
    List<Video> getUserVideos(Integer userId);
    
    /**
     * 获取所有视频列表
     */
    List<Video> getAllVideos();
    
    /**
     * 获取视频详情
     * @param id 视频ID
     * @return 视频对象
     */
    Video getVideoById(Integer id);
    
    /**
     * 删除视频
     * @param id 视频ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteVideo(Integer id, Integer userId);
    
    /**
     * 更新视频信息
     * @param video 视频对象
     * @return 是否更新成功
     */
    boolean updateVideo(Video video);
    
    /**
     * 获取视频上传路径
     */
    String getUploadPath();
} 