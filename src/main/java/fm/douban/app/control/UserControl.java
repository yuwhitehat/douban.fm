package fm.douban.app.control;

import fm.douban.model.User;
import fm.douban.model.UserLoginInfo;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserControl {

    @Autowired
    private UserService userService;

    /**
     * 注册页
     * @param model
     * @return
     */
    @GetMapping(path = "/sign")
    public String signPage(Model model) {
        User user = new User();
        model.addAttribute("user",user);
        return "sign";
    }

    /**
     * 提交注册
     * @param name
     * @param password
     * @param mobile
     * @param request
     * @param response
     * @return
     */
    @PostMapping(path = "/register")
    //@ResponseBody
    public String registerAction(Model model,@RequestParam(name = "name") String name, @RequestParam(name = "password")String password, @RequestParam(name = "mobile")String mobile,
                              @RequestParam(name = "confirmPwd") String confirmPwd, HttpServletRequest request, HttpServletResponse response,
                                 @Valid User user,BindingResult error) {
        if (error.hasErrors()) {
            return "sign";
        }

        // 判断注册名是否已存在
        if (getUserByLoginName(name) != null) {

            model.addAttribute("message1","register name already exist");
            return "sign";
        }

        //记录用户信息
        //user = new User();
        user.setName(name);
        user.setMobile(mobile);
        if (password.equals(confirmPwd)) {
            user.setPassword(password);
        } else {

            model.addAttribute("message2","password confirmed is not correct");
            return "sign";
        }
        User newUser = userService.add(user);
        if (newUser != null && StringUtils.hasText(newUser.getId())) {
            return "redirect:/index";
        }else {
            model.addAttribute("message3","register failed");
            return "sign";
        }

    }

    /**
     * 登录页
     * @param model
     * @return
     */
    @GetMapping(path = "/login")
    public String loginPage(Model model) {

        return "login";
    }

    /**
     * 登录操作
     * @param name
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping(path = "/authenticate")
    //@ResponseBody
    public String login(Model model,@RequestParam(name = "name") String name, @RequestParam(name = "password")String password,
                     HttpServletRequest request,HttpServletResponse response) {

        User loginUser = getUserByLoginName(name);

        //如果查不到用户
        if (loginUser == null) {

            model.addAttribute("message4","userName is not correct");
            return "login";
        }

        if (loginUser.getPassword().equals(password)) {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId("12223334445556688aabbcc");
            userLoginInfo.setUserName(name);
            HttpSession session = request.getSession();
            session.setAttribute("userLoginInfo", userLoginInfo);
            return "redirect:/index";
        } else {
            model.addAttribute("message5","password is not correct");
            return "login";
        }

    }

    /**
     * 通过登录名查找用户，如果查找到返回第一个用户
     * @param loginName
     * @return
     */
    private User getUserByLoginName(String loginName){
        User user = null;
        UserQueryParam userQueryParam = new UserQueryParam();
        userQueryParam.setName(loginName);
        Page<User> users = userService.list(userQueryParam);
        if (users != null && users.getContent() != null && users.getContent().size() > 0) {
            user = users.getContent().get(0);
        }
        return user;
    }

}
