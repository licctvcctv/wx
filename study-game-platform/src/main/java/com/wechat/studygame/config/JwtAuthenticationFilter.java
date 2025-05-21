package com.wechat.studygame.config;

import com.wechat.studygame.service.AdminService;
import com.wechat.studygame.service.JwtService;
import com.wechat.studygame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 不需要验证JWT的路径
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/**",
            "/api/admin/auth/**",
            "/",
            "/api-test.html",
            "/admin/**",
            "/static/**",
            "/swagger-ui.html", 
            "/swagger-resources/**", 
            "/webjars/**", 
            "/v2/api-docs"
    );

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        
        // 检查是否是OPTIONS请求（预检请求）
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        // 检查请求路径是否在排除列表中
        return excludedPaths.stream()
                .anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
                String username = jwtService.getUsernameFromToken(jwt);
                
                // 检查请求路径是否是管理员API
                String requestPath = request.getRequestURI();
                boolean isAdminRequest = requestPath.startsWith("/api/admin/");
                
                UserDetails userDetails;
                try {
                    // 如果是管理员API请求，尝试加载管理员用户
                    if (isAdminRequest) {
                        userDetails = new User(
                            username, "", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    } else {
                        // 普通用户请求
                        userDetails = userService.loadUserByUsername(username);
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    logger.error("用户认证失败: " + username, e);
                }
            }
        } catch (Exception ex) {
            logger.error("无法设置用户认证", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
