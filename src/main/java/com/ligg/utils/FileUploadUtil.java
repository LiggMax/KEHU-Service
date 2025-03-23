package com.ligg.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtil {

    // 头像保存路径，从配置文件中读取
    @Value("${avatar.upload.path:avatars}")
    private String avatarUploadPath;

    // 允许的图片文件扩展名
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * 上传头像
     * @param file 头像文件
     * @return 头像URL
     * @throws IOException 如果保存文件失败
     */
    public String uploadAvatar(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("头像文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") 
            && !contentType.startsWith("image/gif") && !contentType.startsWith("image/webp"))) {
            throw new IllegalArgumentException("不支持的图片格式，只允许JPG、PNG、GIF和WEBP格式");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // 根据内容类型设置默认扩展名
            if (contentType.startsWith("image/jpeg")) {
                extension = ".jpg";
            } else if (contentType.startsWith("image/png")) {
                extension = ".png";
            } else if (contentType.startsWith("image/gif")) {
                extension = ".gif";
            } else if (contentType.startsWith("image/webp")) {
                extension = ".webp";
            }
        }

        // 生成唯一的文件名
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        return uploadAvatar(file, uniqueFilename);
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @param filename 自定义文件名
     * @return 头像URL
     * @throws IOException 如果保存文件失败
     */
    public String uploadAvatar(MultipartFile file, String filename) throws IOException {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("头像文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") 
            && !contentType.startsWith("image/gif") && !contentType.startsWith("image/webp"))) {
            throw new IllegalArgumentException("不支持的图片格式，只允许JPG、PNG、GIF和WEBP格式");
        }

        // 创建保存目录
        Path uploadDir = Paths.get(avatarUploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // 返回访问URL
        return "/user/avatar/" + filename;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * 获取头像保存路径
     */
    public String getAvatarUploadPath() {
        return avatarUploadPath;
    }
} 