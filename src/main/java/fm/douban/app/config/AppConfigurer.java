package fm.douban.app.config;

import fm.douban.app.interceptor.UserInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class AppConfigurer implements WebMvcConfigurer {

    /**
     * 为登录配置拦截页面
     * @param registry
     */
    public void addInterceptors (InterceptorRegistry registry){
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/authenticate") // 登录操作不需要登录
                .excludePathPatterns("/login")        // 登录页面不需要登录
                .excludePathPatterns("/sign")        // 注册页面不需要登录
                .excludePathPatterns("/register")        // 注册操作不需要登录
                .excludePathPatterns("/css/**")           // 静态资源为文件不需要登录
                .excludePathPatterns("/error");            // 系统错误页面不需要登录
    }
}
