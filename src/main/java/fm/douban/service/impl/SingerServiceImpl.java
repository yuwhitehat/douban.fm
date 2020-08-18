package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
public class SingerServiceImpl implements SingerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(SingerServiceImpl.class);
    /**
     * 增加一个歌手
     * @param singer
     * @return
     */
    @Override
    public Singer addSinger(Singer singer) {
        if (singer == null) {
            LOG.error("Singer data is null");
            return null;
        }

        return mongoTemplate.insert(singer);
    }
    /**
     *根据歌手id查询歌手
     * @param singerId
     * @return
     */
    @Override
    public Singer get(String singerId) {
        if (!StringUtils.hasText(singerId)){
            LOG.error("singerId is null");
            return null;
        }
        return mongoTemplate.findById(singerId, Singer.class);
    }
    /**
     * 查询全部歌手
     * @return
     */
    @Override
    public List<Singer> getAll() {

        return mongoTemplate.findAll(Singer.class);
    }
    /**
     * 修改歌手，只能修改名称、头像、主页、相似的歌手id
     * @param singer
     * @return
     */
    @Override
    public boolean modify(Singer singer) {
        if (singer == null || !StringUtils.hasText(singer.getId())){
            LOG.error("singer data or id is null");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(singer.getId()));
        Update update = new Update();
        if (singer.getName() != null) {
            update.set("name",singer.getName());
        }
        if (singer.getAvatar() != null) {
            update.set("name",singer.getAvatar());
        }
        if (singer.getHomePage() != null) {
            update.set("name",singer.getHomePage());
        }
        if (singer.getSimilarSingerIds() != null) {
            update.set("name",singer.getSimilarSingerIds());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, Singer.class);
        return result != null && result.getModifiedCount() > 0;

    }
    /**
     * 根据id删除歌手
     * @param singerId
     * @return
     */
    @Override
    public boolean delete(String singerId) {

        if (!StringUtils.hasText(singerId)){
            LOG.error("id is null");
            return false;
        }
        Singer singer = new Singer();
        singer.setId(singerId);
        DeleteResult result = mongoTemplate.remove(singer);
        return result != null && result.getDeletedCount() > 0;
    }
}
