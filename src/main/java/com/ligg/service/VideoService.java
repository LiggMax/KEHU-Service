package com.ligg.service;

import com.ligg.pojo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VideoService {
    
    /**
     * 保存视频信息
     */
    Video saveVideo(String title, String description, MultipartFile videoFile, MultipartFile coverFile, Integer userId);
    
    /**
     * 保存视频信息（带分类）
     */
    Video saveVideo(String title, String description, String category, MultipartFile videoFile, MultipartFile coverFile, Integer userId);
    
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
     * @param id 视频ID
     * @param userId 用户ID
     * @return 包含删除结果详情的Map，包括数据库记录删除状态和物理文件删除状态
     */
    Map<String, Object> deleteVideo(Integer id, Integer userId);
    
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
    
    /**
     * 搜索视频
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页条数
     * @return 搜索结果和分页信息
     */
    Map<String, Object> searchVideos(String keyword, Integer page, Integer size);
    
    /**
     * 获取热门搜索关键词
     * @return 热门搜索关键词列表
     */
    List<String> getHotSearchKeywords();
    
    /**
     * 获取搜索建议
     * @param keyword 用户输入的关键词
     * @return 搜索建议列表
     */
    List<String> getSearchSuggestions(String keyword);
    
    /**
     * 增加视频观看次数
     * @param videoId 视频ID
     * @return 增加是否成功
     */
    boolean incrementViewCount(Integer videoId);
    
    /**
     * 获取所有分类
     * @return 所有分类的列表
     */
    List<String> getAllCategories();
    
    /**
     * 获取所有分类及其视频数量
     * @return 包含分类及其计数的Map
     */
    Map<String, Object> getCategoriesWithCount();
    
    /**
     * 根据分类获取视频
     * @param category 分类名称
     * @return 该分类下的视频列表
     */
    List<Video> getVideosByCategory(String category);
    
    /**
     * 分页获取分类下的视频
     * @param category 分类名称
     * @param page 页码
     * @param size 每页数量
     * @return 分页后的视频列表和分页信息
     */
    Map<String, Object> getVideosByCategoryPaged(String category, Integer page, Integer size);
} 