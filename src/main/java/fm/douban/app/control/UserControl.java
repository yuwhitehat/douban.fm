package fm.douban.app.control;

import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @ResponseBody
    public Map registerAction(@RequestParam(name = "name") String name, @RequestParam(name = "password")String password, @RequestParam(name = "mobile")String mobile,
                              @RequestParam(name = "confirmPwd") String confirmPwd,HttpServletRequest request, HttpServletResponse response) {
        Map returnMap = new HashMap();
        // 判断登录名是否已存在
        if (getUserByLoginName(name) != null) {
            returnMap.put("result", false);
            returnMap.put("message", "login name already exist");
            return returnMap;
        }

        //记录用户信息
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        if (password.equals(confirmPwd)) {
            user.setPassword(password);
        }
        User newUser = userService.add(user);
        if (newUser != null && StringUtils.hasText(newUser.getId())) {
            returnMap.put("result",true);
            returnMap.put("message","register successful");
        }else {
            returnMap.put("result",false);
            returnMap.put("message","register failed");
        }
        return returnMap;
    }

    /**
     * 查找已经登陆的用户
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
