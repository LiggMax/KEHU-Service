package com.ligg.service.impl;

import com.ligg.mapper.VideoMapper;
import com.ligg.pojo.Video;
import com.ligg.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;
    
    // 视频保存路径，从配置文件中读取
    @Value("${video.upload.path:videos}")
    private String uploadPath;
    
    // 允许的视频文件扩展名
    private static final String[] ALLOWED_EXTENSIONS = {".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv"};
    
    @Override
    public Video saveVideo(String title, String description, MultipartFile videoFile, Integer userId) {
        // 检查视频文件是否为空
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("视频文件不能为空");
        }
        
        // 获取原始文件名
        String originalFilename = videoFile.getOriginalFilename();
        
        // 检查文件扩展名
        if (!isValidVideoFile(originalFilename)) {
            throw new IllegalArgumentException("不支持的视频文件格式");
        }
        
        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 视频文件保存路径
            String filePath = uploadPath + File.separator + newFilename;
            Path path = Paths.get(filePath);
            
            // 保存文件
            Files.copy(videoFile.getInputStream(), path);
            
            // 临时使用默认封面URL
            String coverUrl = "default_cover.jpg";
            
            // 创建视频对象
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setFilePath("/api/video/stream/" + newFilename);
            video.setCoverUrl(coverUrl);
            video.setUserId(userId);
            video.setViewCount(0); // 初始观看次数为0
            Date now = new Date();
            video.setCreateTime(now);
            video.setUpdateTime(now);
            
            // 打印关键信息
            System.out.println("保存视频: 标题=" + title + ", 用户ID=" + userId + ", 文件路径=" + filePath);
            
            // 保存到数据库
            videoMapper.saveVideo(video);
            
            return video;
        } catch (IOException e) {
            throw new RuntimeException("保存视频文件失败", e);
        }
    }
    
    @Override
    public List<Video> getUserVideos(Integer userId) {
        return videoMapper.getVideosByUserId(userId);
    }
    
    @Override
    public List<Video> getAllVideos() {
        return videoMapper.getAllVideos();
    }
    
    @Override
    public Video getVideoById(Integer id) {
        return videoMapper.getVideoById(id);
    }
    
    @Override
    public boolean deleteVideo(Integer id, Integer userId) {
        // 获取视频信息
        Video video = videoMapper.getVideoById(id);
        
        // 检查视频是否存在且属于当前用户
        if (video == null || !video.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除数据库记录
        int result = videoMapper.deleteVideo(id, userId);
        
        // 如果删除成功，同时删除文件系统中的视频文件
        if (result > 0) {
            try {
                // 删除视频文件
                Path videoPath = Paths.get(video.getFilePath());
                if (Files.exists(videoPath)) {
                    Files.delete(videoPath);
                }
                return true;
            } catch (IOException e) {
                // 记录日志但不影响返回结果
                e.printStackTrace();
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean updateVideo(Video video) {
        if (video == null || video.getId() == null) {
            return false;
        }
        
        // 设置更新时间
        video.setUpdateTime(new Date());
        
        // 更新视频信息
        int result = videoMapper.updateVideo(video);
        return result > 0;
    }
    
    @Override
    public String getUploadPath() {
        return this.uploadPath;
    }
    
    /**
     * 检查文件是否为有效的视频文件
     */
    private boolean isValidVideoFile(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }
} 