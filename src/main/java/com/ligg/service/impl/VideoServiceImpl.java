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
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.ligg.pojo.User;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;
    
    // 视频保存路径，从配置文件中读取
    @Value("${video.upload.path:videos}")
    private String uploadPath;
    
    // 封面图片保存路径，从配置文件中读取
    @Value("${cover.upload.path:covers}")
    private String coverUploadPath;
    
    // 允许的视频文件扩展名
    private static final String[] ALLOWED_VIDEO_EXTENSIONS = {".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv"};
    
    // 允许的图片文件扩展名
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    
    @Override
    public Video saveVideo(String title, String description, MultipartFile videoFile, MultipartFile coverFile, Integer userId) {
        // 调用带分类的方法，传入null作为分类
        return saveVideo(title, description, null, videoFile, coverFile, userId);
    }
    
    @Override
    public Video saveVideo(String title, String description, String category, MultipartFile videoFile, MultipartFile coverFile, Integer userId) {
        // 检查视频文件是否为空
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("视频文件不能为空");
        }
        
        // 检查用户ID是否为空
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
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
            
            // 确保封面上传目录存在
            File coverDir = new File(coverUploadPath);
            if (!coverDir.exists()) {
                coverDir.mkdirs();
            }
            
            // 生成唯一文件名
            String videoExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newVideoFilename = UUID.randomUUID().toString() + videoExtension;
            
            // 视频文件保存路径
            String filePath = uploadPath + File.separator + newVideoFilename;
            Path videoPath = Paths.get(filePath);
            
            // 保存视频文件
            Files.copy(videoFile.getInputStream(), videoPath);
            
            // 处理封面图片
            String videoImgPath = null;
            if (coverFile != null && !coverFile.isEmpty()) {
                String coverOriginalFilename = coverFile.getOriginalFilename();
                if (isValidImageFile(coverOriginalFilename)) {
                    String coverExtension = coverOriginalFilename.substring(coverOriginalFilename.lastIndexOf("."));
                    String newCoverFilename = UUID.randomUUID().toString() + coverExtension;
                    String coverPath = coverUploadPath + File.separator + newCoverFilename;
                    Path imagePath = Paths.get(coverPath);
                    
                    // 保存封面图片
                    Files.copy(coverFile.getInputStream(), imagePath);
                    videoImgPath = "/api/video/cover/" + newCoverFilename;
                }
            }
            
            // 创建视频对象
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setCategory(category); // 设置分类
            video.setFilePath("/api/video/stream/" + newVideoFilename);
            video.setCoverUrl("default_cover.jpg"); // 为了兼容性保留
            video.setVideoImg(videoImgPath);
            video.setUserId(userId);
            video.setViewCount(0); // 初始观看次数为0
            Date now = new Date();
            video.setCreateTime(now);
            video.setUpdateTime(now);
            
            // 打印关键信息
            System.out.println("保存视频: 标题=" + title + ", 分类=" + category + ", 用户ID=" + userId + ", 视频路径=" + filePath + ", 封面路径=" + videoImgPath);
            
            // 保存到数据库
            videoMapper.saveVideo(video);
            
            return video;
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败", e);
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
    public Map<String, Object> deleteVideo(Integer id, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("dbDeleted", false);
        result.put("videoFileDeleted", false);
        result.put("coverFileDeleted", false);
        
        // 获取视频信息
        Video video = videoMapper.getVideoById(id);
        
        // 检查视频是否存在且属于当前用户
        if (video == null || !video.getUserId().equals(userId)) {
            System.out.println("删除视频失败：视频不存在或不属于当前用户, videoId=" + id + ", userId=" + userId);
            result.put("errorMsg", "视频不存在或不属于当前用户");
            return result;
        }
        
        // 删除数据库记录
        int dbResult = videoMapper.deleteVideo(id, userId);
        
        // 如果删除数据库记录成功，同时删除文件系统中的视频文件和封面文件
        if (dbResult > 0) {
            result.put("dbDeleted", true);
            
            try {
                System.out.println("开始删除视频文件: videoId=" + id);
                boolean videoDeleted = false;
                boolean coverDeleted = false;
                
                // 删除视频文件
                if (video.getFilePath() != null && video.getFilePath().startsWith("/api/video/stream/")) {
                    String filename = video.getFilePath().substring("/api/video/stream/".length());
                    Path videoPath = Paths.get(uploadPath, filename);
                    if (Files.exists(videoPath)) {
                        Files.delete(videoPath);
                        videoDeleted = true;
                        System.out.println("成功删除视频文件: " + videoPath);
                    } else {
                        System.out.println("视频文件不存在: " + videoPath);
                        // 如果文件不存在，也算是删除成功
                        videoDeleted = true;
                    }
                } else {
                    // 如果没有视频路径，视为删除成功
                    videoDeleted = true;
                }
                
                // 删除封面文件
                if (video.getVideoImg() != null && video.getVideoImg().startsWith("/api/video/cover/")) {
                    String coverFilename = video.getVideoImg().substring("/api/video/cover/".length());
                    Path coverPath = Paths.get(coverUploadPath, coverFilename);
                    if (Files.exists(coverPath)) {
                        Files.delete(coverPath);
                        coverDeleted = true;
                        System.out.println("成功删除封面文件: " + coverPath);
                    } else {
                        System.out.println("封面文件不存在: " + coverPath);
                        // 如果文件不存在，也算是删除成功
                        coverDeleted = true;
                    }
                } else {
                    // 如果没有封面路径，视为删除成功
                    coverDeleted = true;
                }
                
                result.put("videoFileDeleted", videoDeleted);
                result.put("coverFileDeleted", coverDeleted);
                result.put("success", videoDeleted && coverDeleted);
                
            } catch (IOException e) {
                // 文件删除失败
                System.err.println("删除视频文件出错: " + e.getMessage());
                e.printStackTrace();
                result.put("errorMsg", "物理文件删除失败: " + e.getMessage());
            }
        } else {
            result.put("errorMsg", "数据库记录删除失败");
        }
        
        return result;
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
    public boolean updateVideoCover(Integer videoId, MultipartFile coverFile, Integer userId) {
        // 检查参数
        if (videoId == null || coverFile == null || coverFile.isEmpty() || userId == null) {
            return false;
        }
        
        // 获取视频信息
        Video video = videoMapper.getVideoById(videoId);
        if (video == null || !video.getUserId().equals(userId)) {
            return false;
        }
        
        try {
            // 确保封面上传目录存在
            File coverDir = new File(coverUploadPath);
            if (!coverDir.exists()) {
                coverDir.mkdirs();
            }
            
            // 处理封面图片
            String coverOriginalFilename = coverFile.getOriginalFilename();
            if (!isValidImageFile(coverOriginalFilename)) {
                return false;
            }
            
            // 生成新的封面文件名
            String coverExtension = coverOriginalFilename.substring(coverOriginalFilename.lastIndexOf("."));
            String newCoverFilename = UUID.randomUUID().toString() + coverExtension;
            String coverPath = coverUploadPath + File.separator + newCoverFilename;
            Path imagePath = Paths.get(coverPath);
            
            // 保存新封面
            Files.copy(coverFile.getInputStream(), imagePath);
            
            // 删除旧封面(如果存在)
            if (video.getVideoImg() != null && video.getVideoImg().startsWith("/api/video/cover/")) {
                String oldCoverFilename = video.getVideoImg().substring("/api/video/cover/".length());
                Path oldCoverPath = Paths.get(coverUploadPath, oldCoverFilename);
                if (Files.exists(oldCoverPath)) {
                    Files.delete(oldCoverPath);
                }
            }
            
            // 更新视频封面路径
            video.setVideoImg("/api/video/cover/" + newCoverFilename);
            video.setUpdateTime(new Date());
            
            // 更新数据库
            int result = videoMapper.updateVideo(video);
            return result > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String getUploadPath() {
        return this.uploadPath;
    }
    
    @Override
    public String getCoverUploadPath() {
        return this.coverUploadPath;
    }
    
    /**
     * 检查文件是否为有效的视频文件
     */
    private boolean isValidVideoFile(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        for (String ext : ALLOWED_VIDEO_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查文件是否为有效的图片文件
     */
    private boolean isValidImageFile(String filename) {
        if (!StringUtils.hasText(filename)) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Map<String, Object> searchVideos(String keyword, Integer page, Integer size) {
        Map<String, Object> result = new HashMap<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", 1);
            result.put("size", 10);
            return result;
        }
        
        // 处理参数
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;
        int offset = (currentPage - 1) * pageSize;
        
        try {
            // 获取分页数据
            List<Video> videos = videoMapper.searchVideosPaged(keyword, offset, pageSize);
            
            // 获取总记录数
            int total = videoMapper.countSearchResults(keyword);
            
            // 构建结果
            result.put("list", videos);
            result.put("total", total);
            result.put("page", currentPage);
            result.put("size", pageSize);
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            
            // 发生异常时返回空结果
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", currentPage);
            result.put("size", pageSize);
            
            return result;
        }
    }
    
    @Override
    public List<String> getHotSearchKeywords() {
        return videoMapper.getHotSearchKeywords();
    }
    
    @Override
    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        try {
            // 基于视频标题、描述和分类生成建议
            List<Video> videos = videoMapper.searchVideos(keyword);
            Set<String> suggestions = new HashSet<>();
            
            // 从视频标题和分类中提取建议
            for (Video video : videos) {
                if (video.getTitle() != null) {
                    suggestions.add(video.getTitle());
                }
                if (video.getCategory() != null && !video.getCategory().isEmpty()) {
                    suggestions.add(video.getCategory());
                }
            }
            
            return new ArrayList<>(suggestions);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public boolean incrementViewCount(Integer videoId) {
        if (videoId == null) {
            return false;
        }
        
        try {
            // 调用Mapper层增加观看次数
            videoMapper.incrementViewCount(videoId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> getAllCategories() {
        try {
            return videoMapper.getAllCategories();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getCategoriesWithCount() {
        try {
            // 获取带计数的分类列表
            List<Map<String, Object>> categoriesWithCount = videoMapper.getCategoriesWithCount();
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            List<String> categories = new ArrayList<>();
            Map<String, Integer> counts = new HashMap<>();
            
            // 整理数据格式
            for (Map<String, Object> item : categoriesWithCount) {
                String category = (String) item.get("key");
                Integer count = ((Number) item.get("value")).intValue();
                
                categories.add(category);
                counts.put(category, count);
            }
            
            result.put("categories", categories);
            result.put("counts", counts);
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("categories", List.of(), "counts", Map.of());
        }
    }
    
    @Override
    public List<Video> getVideosByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return videoMapper.getVideosByCategory(category);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getVideosByCategoryPaged(String category, Integer page, Integer size) {
        Map<String, Object> result = new HashMap<>();
        
        if (category == null || category.trim().isEmpty()) {
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", 1);
            result.put("size", 10);
            return result;
        }
        
        // 处理参数
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;
        int offset = (currentPage - 1) * pageSize;
        
        try {
            // 获取分页数据
            List<Video> videos = videoMapper.getVideosByCategoryPaged(category, offset, pageSize);
            
            // 获取总记录数
            int total = videoMapper.countVideosByCategory(category);
            
            // 构建结果
            result.put("list", videos);
            result.put("total", total);
            result.put("page", currentPage);
            result.put("size", pageSize);
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            
            // 发生异常时返回空结果
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            result.put("page", currentPage);
            result.put("size", pageSize);
            
            return result;
        }
    }
} 