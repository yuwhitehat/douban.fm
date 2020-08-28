package fm.douban.model;

import java.io.Serializable;

/**
 * 登录信息类
 * 因为要在网络上传输，必须实现序列化接口
 */
public class UserLoginInfo implements Serializable {

    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
