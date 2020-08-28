package fm.douban.app.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户信息拦截器
 */
public class UserInterceptor implements HandlerInterceptor {

    /**
     * controller方法执行之前
     * 判断当前页面是否有用户信息session，如果没有就跳转到登录页
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("userLoginInfo") != null) {
            return true;
        }
        String url = "/login";
        response.sendRedirect(url);
        return false;
    }
    //controller执行之后
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //整个请求完成后（包括Thymeleaf渲染完毕）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
