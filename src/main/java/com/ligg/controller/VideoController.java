package com.ligg.controller;

import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.pojo.Video;
import com.ligg.service.VideoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;


    /**
     * 获取所有视频列表
     */
    @GetMapping("/all")
    public Result<List<Video>> getAllVideos() {
        // 调用服务层获取所有视频列表
        List<Video> videos = videoService.getAllVideos();
        return Result.success(videos);
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/{id}")
    public Result<Video> getVideoById(@PathVariable Integer id) {
        Video video = videoService.getVideoById(id);
        if (video == null) {
            return Result.error("视频不存在");
        }
        return Result.success(video);
    }

    /**
     * 流式获取视频内容
     */
    @GetMapping("/stream/{filename:.+}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) {
        try {
            // 获取视频服务注入的上传路径
            String uploadPath = videoService.getUploadPath();

            // 构建视频文件的完整路径
            Path videoPath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(videoPath.toUri());

            // 检查资源是否存在
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 确定内容类型
            String contentType = determineContentType(filename);

            // 返回视频资源，支持范围请求
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据文件名确定内容类型
     */
    private String determineContentType(String filename) {
        if (filename.endsWith(".mp4")) {
            return "video/mp4";
        } else if (filename.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (filename.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        } else if (filename.endsWith(".mov")) {
            return "video/quicktime";
        } else if (filename.endsWith(".flv")) {
            return "video/x-flv";
        } else if (filename.endsWith(".mkv")) {
            return "video/x-matroska";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 获取视频封面图片
     */
    @GetMapping("/cover/{filename:.+}")
    public ResponseEntity<Resource> getCoverImage(@PathVariable String filename) {
        try {
            // 获取封面上传路径
            String coverUploadPath = videoService.getCoverUploadPath();

            // 构建封面文件的完整路径
            Path coverPath = Paths.get(coverUploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(coverPath.toUri());

            // 检查资源是否存在
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 确定内容类型
            String contentType = "image/jpeg"; // 默认JPEG
            if (filename.endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.endsWith(".webp")) {
                contentType = "image/webp";
            }

            // 返回图片资源
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/categories")
    public Result<Map<String, Object>> getAllCategories() {
        Map<String, Object> result = videoService.getCategoriesWithCount();
        return Result.success(result);
    }

    /**
     * 根据分类获取视频列表
     */
    @GetMapping("/category/{categoryName}/all")
    public Result<List<Video>> getVideosByCategory(@PathVariable String categoryName) {
        List<Video> videos = videoService.getVideosByCategory(categoryName);
        return Result.success(videos);
    }

    /**
     * 根据分类获取视频列表（分页）
     */
    @GetMapping("/category/{categoryName}")
    public Result<Map<String, Object>> getVideosByCategoryPaged(
            @PathVariable String categoryName,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size) {
        Map<String, Object> result = videoService.getVideosByCategoryPaged(categoryName, page, size);
        return Result.success(result);
    }

    /**
     * 搜索视频
     */
    @GetMapping("/search")
    public Result<Map<String, Object>> searchVideos(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Map<String, Object> result = videoService.searchVideos(keyword, page, size);
        return Result.success(result);
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/search/hot")
    public Result<List<String>> getHotSearchKeywords() {
        List<String> keywords = videoService.getHotSearchKeywords();
        return Result.success(keywords);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/search/suggest")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        List<String> suggestions = videoService.getSearchSuggestions(keyword);
        return Result.success(suggestions);
    }

    /**
     * 增加视频观看次数
     */
    @PostMapping("/{id}/view")
    public Result<String> incrementViewCount(@PathVariable Integer id) {
        boolean success = videoService.incrementViewCount(id);
        if (success) {
            return Result.success("增加观看次数成功");
        } else {
            return Result.error("增加观看次数失败");
        }
    }
} 