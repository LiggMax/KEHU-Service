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
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 上传视频
     */
    @PostMapping("/upload")
    public Result<Video> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoFile") MultipartFile videoFile,
            HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        try {
            // 调用服务层保存视频
            Video video = videoService.saveVideo(title, description, videoFile, loginUser.getUserId());
            return Result.success(video);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("视频上传失败");
        }
    }

    /**
     * 获取当前用户的视频列表
     */
    @GetMapping("/list")
    public Result<List<Video>> getUserVideos(HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 调用服务层获取视频列表
        List<Video> videos = videoService.getUserVideos(loginUser.getUserId());
        return Result.success(videos);
    }

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
     * 删除视频
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteVideo(@PathVariable Integer id, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 调用服务层删除视频
        boolean success = videoService.deleteVideo(id, loginUser.getUserId());
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，视频不存在或不属于当前用户");
        }
    }

    /**
     * 更新视频信息
     */
    @PutMapping("/{id}")
    public Result<String> updateVideo(
            @PathVariable Integer id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 获取原视频信息
        Video video = videoService.getVideoById(id);
        if (video == null || !video.getUserId().equals(loginUser.getUserId())) {
            return Result.error("视频不存在或不属于当前用户");
        }

        // 更新视频信息
        video.setTitle(title);
        video.setDescription(description);
        boolean success = videoService.updateVideo(video);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
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
} 