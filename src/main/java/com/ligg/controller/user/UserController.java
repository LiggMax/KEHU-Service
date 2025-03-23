package com.ligg.controller.user;


import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.pojo.Video;
import com.ligg.service.VideoService;
import com.ligg.utils.FileUploadUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ligg.mapper.UserMapper;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoService videoService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return Result.success();
    }

    /**
     * 上传视频
     */
    @PostMapping("/video/upload")
    public Result<Video> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        try {
            // 调用服务层保存视频
            Video video = videoService.saveVideo(title, description, category, videoFile, coverFile, loginUser.getUserId());
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
    @GetMapping("/video/list")
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
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            return Result.success(loginUser);
        }
        return Result.error("未登录");
    }
    
    /**
     * 删除视频
     */
    @DeleteMapping("/video/{id}")
    public Result<Map<String, Object>> deleteVideo(@PathVariable Integer id, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 获取视频信息用于后续返回
        Video video = videoService.getVideoById(id);
        if (video == null) {
            return Result.error("视频不存在");
        }
        
        // 调用服务层删除视频
        Map<String, Object> deleteResult = videoService.deleteVideo(id, loginUser.getUserId());
        
        // 返回结果中添加视频的基本信息
        deleteResult.put("id", id);
        deleteResult.put("title", video.getTitle());
        deleteResult.put("videoPath", video.getFilePath());
        deleteResult.put("coverPath", video.getVideoImg());
        
        // 根据删除结果返回成功或失败
        if (Boolean.TRUE.equals(deleteResult.get("success")) || Boolean.TRUE.equals(deleteResult.get("dbDeleted"))) {
            return Result.success(deleteResult);
        } else {
            String errorMsg = (String) deleteResult.getOrDefault("errorMsg", "删除失败，视频不存在或不属于当前用户");
            return Result.error(errorMsg);
        }
    }

    /**
     * 更新视频信息
     */
    @PutMapping("/video/{id}")
    public Result<String> updateVideo(
            @PathVariable Integer id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "category", required = false) String category,
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
        video.setCategory(category); // 设置分类
        boolean success = videoService.updateVideo(video);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 更新视频信息（以JSON格式提交）
     */
    @PutMapping(value = "/video/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> updateVideoJson(
            @PathVariable Integer id,
            @RequestBody Video updateVideo,
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

        // 确保ID一致
        if (!id.equals(updateVideo.getId())) {
            return Result.error("视频ID不匹配");
        }

        // 更新视频信息
        video.setTitle(updateVideo.getTitle());
        video.setDescription(updateVideo.getDescription());
        video.setCategory(updateVideo.getCategory());
        boolean success = videoService.updateVideo(video);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 更新视频封面
     */
    @PostMapping("/video/{id}/cover")
    public Result<String> updateVideoCover(
            @PathVariable Integer id,
            @RequestParam("coverFile") MultipartFile coverFile,
            HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        try {
            boolean success = videoService.updateVideoCover(id, coverFile, loginUser.getUserId());
            if (success) {
                return Result.success("封面更新成功");
            } else {
                return Result.error("封面更新失败，视频不存在或不属于当前用户");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("封面更新失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户昵称
     */
    @PutMapping("/nickname")
    public Result<String> updateNickname(@RequestParam String nickname, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }
        
        // 验证昵称
        if (nickname == null || nickname.trim().isEmpty()) {
            return Result.error("昵称不能为空");
        }
        
        if (nickname.length() > 20) {
            return Result.error("昵称长度不能超过20个字符");
        }
        
        try {
            // 更新昵称
            int result = userMapper.updateNickname(loginUser.getUserId(), nickname);
            if (result > 0) {
                // 更新session中的用户信息
                loginUser.setNickname(nickname);
                session.setAttribute("loginUser", loginUser);
                
                return Result.success(nickname);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新昵称失败");
        }
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") 
                && !contentType.startsWith("image/gif") && !contentType.startsWith("image/webp"))) {
                return Result.error("只支持JPEG、PNG、GIF和WEBP格式的图片");
            }

            // 检查文件大小
            if (file.getSize() > 2 * 1024 * 1024) { // 2MB
                return Result.error("图片大小不能超过2MB");
            }

            // 上传头像文件，获取URL
            String avatarUrl = fileUploadUtil.uploadAvatar(file);
            
            // 更新数据库
            int result = userMapper.updateAvatar(loginUser.getUserId(), avatarUrl);
            if (result > 0) {
                // 更新session中的用户信息
                loginUser.setAvatar(avatarUrl);
                session.setAttribute("loginUser", loginUser);
                
                return Result.success(avatarUrl);
            } else {
                return Result.error("更新头像失败");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传头像失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户头像
     */
    @GetMapping("/avatar/{filename}")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable String filename) {
        try {
            // 构建头像文件路径
            Path avatarPath = Paths.get(fileUploadUtil.getAvatarUploadPath(), filename);
            
            if (!Files.exists(avatarPath)) {
                return getDefaultAvatar();
            }
            
            // 确定文件类型
            MediaType mediaType = MediaType.IMAGE_PNG; // 默认类型
            String filenameLC = filename.toLowerCase();
            if (filenameLC.endsWith(".jpg") || filenameLC.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (filenameLC.endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (filenameLC.endsWith(".webp")) {
                mediaType = new MediaType("image", "webp");
            }
            
            // 获取文件长度
            long fileSize = Files.size(avatarPath);
            
            // 使用资源文件处理器
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(avatarPath.toUri());
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(fileSize)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(Files.readAllBytes(avatarPath)); // 如果文件小，保持原来的方法以简化处理
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultAvatar();
        }
    }
    
    /**
     * 获取用户头像（流式方法，适用于大文件）
     */
    @GetMapping("/avatar-stream/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> getUserAvatarAsStream(@PathVariable String filename) {
        try {
            // 构建头像文件路径
            Path avatarPath = Paths.get(fileUploadUtil.getAvatarUploadPath(), filename);
            
            if (!Files.exists(avatarPath)) {
                // 对于流式API，如果文件不存在则重定向到默认头像
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "/user/default-avatar.png")
                        .build();
            }
            
            // 确定文件类型
            MediaType mediaType = MediaType.IMAGE_PNG; // 默认类型
            String filenameLC = filename.toLowerCase();
            if (filenameLC.endsWith(".jpg") || filenameLC.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (filenameLC.endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (filenameLC.endsWith(".webp")) {
                mediaType = new MediaType("image", "webp");
            }
            
            // 创建资源
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(avatarPath.toUri());
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            // 对于流式API，如果出错则重定向到默认头像
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/user/default-avatar.png")
                    .build();
        }
    }

    /**
     * 获取默认头像
     */
    @GetMapping("/default-avatar.png")
    public ResponseEntity<byte[]> getDefaultAvatar() {
        try {
            // 尝试从类路径中获取默认头像
            try {
                Path defaultAvatarPath = Paths.get(getClass().getResource("/static/default-avatar.png").toURI());
                if (Files.exists(defaultAvatarPath)) {
                    byte[] avatarBytes = Files.readAllBytes(defaultAvatarPath);
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_PNG)
                            .body(avatarBytes);
                }
            } catch (Exception e) {
                // 如果静态资源不存在，忽略并继续使用生成的头像
            }
            
            // 如果没有找到静态资源，生成一个带有首字母的简单头像
            int size = 128;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // 绘制圆形背景
            g2d.setColor(new Color(66, 184, 131)); // 绿色背景
            g2d.fillOval(0, 0, size, size);
            
            // 在中间添加用户图标
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 64));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics fm = g2d.getFontMetrics();
            String text = "U";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2d.drawString(text, (size - textWidth) / 2, (size - textHeight) / 2 + fm.getAscent());
            
            g2d.dispose();
            
            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
