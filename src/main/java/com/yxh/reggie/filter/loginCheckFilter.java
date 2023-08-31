package com.yxh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yxh.reggie.common.BaseContext;
import com.yxh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class loginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //获取当前请求路劲
        String requestURI = request.getRequestURI();
        //不需要处理的路径
        String[] uris=new String[]{
                "/employee/login",
                "employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        //判断是否属于不需要处理路径
        boolean check = check(uris, requestURI);
        //属于则放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //账号登录放行
        if (request.getSession().getAttribute("employee")!=null){
            Long employeeId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(request,response);
            return;
        }
        //账号登录放行
        if (request.getSession().getAttribute("user")!=null){
            Long userId=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //未登录输出提示信息
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 检查是否属于不需要处理路径
     * @param uris
     * @param requestURI
     * @return
     */
    public boolean check(String[] uris,String requestURI){
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
