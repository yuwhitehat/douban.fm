package fm.douban.service;

import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import org.springframework.data.domain.Page;

public interface UserService {
    /**
     * 添加一个用户
     * @param user
     * @return
     */
    public User add(User user);

    /**
     * 通过id查询用户
     * @param id
     */
    public User get(String id);

    /**
     * 分页查询用户
     * @param userQueryParam
     * @return
     */
    public Page<User> list(UserQueryParam userQueryParam);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    public boolean modify(User user);

    /**
     * 删除一条用户信息
     * @param id
     * @return
     */
    public boolean delete(String id);
}
