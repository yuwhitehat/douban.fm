package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加一个用户
     * @param user
     * @return
     */
    @Override
    public User add(User user) {
        if (user == null) {
            LOG.error("user data is null");
            return null;
        }
        return mongoTemplate.insert(user);
    }

    /**
     * 通过id查询用户
     * @param id
     */
    @Override
    public User get(String id) {
        if (!StringUtils.hasText(id)){
            LOG.error("userId is empty");
            return null;
        }
        return mongoTemplate.findById(id,User.class);
    }

    /**
     * 分页查询用户
     * @param userQueryParam
     * @return
     */
    @Override
    public Page<User> list(UserQueryParam userQueryParam) {
        if (userQueryParam == null) {
            LOG.error("user data is null");
            return null;
        }
        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();
        if (StringUtils.hasText(userQueryParam.getName())) {
            subCris.add(Criteria.where("name").is(userQueryParam.getName()));
        }
        if (StringUtils.hasText(userQueryParam.getMobile())) {
            subCris.add(Criteria.where("mobile").is(userQueryParam.getMobile()));
        }
        criteria.andOperator(subCris.toArray(new Criteria[]{}));
        Query query = new Query(criteria);
        Pageable pageable = PageRequest.of(userQueryParam.getPageNum() - 1,userQueryParam.getPageSize());
        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);
        long count = mongoTemplate.count(query, User.class);
        Page<User> pageResult = PageableExecutionUtils.getPage(users, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });
        return pageResult;
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @Override
    public boolean modify(User user) {
        if (user == null || !StringUtils.hasText(user.getId())) {
            LOG.error("user data or id is null");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(user.getId()));
        Update update = new Update();
        if (user.getMobile() != null) {
            update.set("mobile", user.getMobile());
        }
        if (user.getName() != null) {
            update.set("name", user.getName());
        }
        if (user.getPassword() != null) {
            update.set("password", user.getPassword());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);
        return result != null && result.getModifiedCount() > 0;
    }

    /**
     * 删除一条用户信息
     * @param id
     * @return
     */
    @Override
    public boolean delete(String id) {
        if (!StringUtils.hasText(id)){
            LOG.error("userId is null");
            return false;
        }
        User user = new User();
        user.setId(id);
        DeleteResult result = mongoTemplate.remove(user);
        return result != null && result.getDeletedCount() > 0;
    }
}
