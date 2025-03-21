package com.ligg.interceptor;

import com.ligg.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取session中的用户信息
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        // 未登录
        if (loginUser == null) {
            response.setStatus(401); // 设置状态码为未授权
            return false;
        }
        
        // 已登录，放行
        return true;
    }
} 