package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import fm.douban.model.Favorite;
import fm.douban.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 新增一个喜欢
     * @param fav
     * @return
     */
    @Override
    public Favorite add(Favorite fav) {
        if (fav == null) {
            LOG.error("fav data is null");
            return null;
        }
        if (fav.getGmtCreated() == null) {
            fav.setGmtCreated(LocalDateTime.now());
        }
        if (fav.getGmtModified() == null) {
            fav.setGmtModified(LocalDateTime.now());
        }
        return mongoTemplate.insert(fav);

    }

    /**
     * 计算喜欢数，如果数大于0，表示已经喜欢
     * @param favParam
     * @return
     */
    @Override
    public List<Favorite> list(Favorite favParam) {
        if (favParam == null) {
            LOG.error("favParam data is null");
            return null;
        }
        // 查询总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();

        if (StringUtils.hasText(favParam.getType())) {
            subCris.add(Criteria.where("type").is(favParam.getType()));
        }
        if (StringUtils.hasText(favParam.getUserId())) {
            subCris.add(Criteria.where("userId").is(favParam.getUserId()));
        }

        if (StringUtils.hasText(favParam.getItemType())){
            subCris.add(Criteria.where("itemType").is(favParam.getItemType()));
        }
        if (StringUtils.hasText(favParam.getItemId())){
            subCris.add(Criteria.where("itemId").is(favParam.getItemId()));
        }
        if (!subCris.isEmpty()) {
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }

        Query query = new Query(criteria);

        return mongoTemplate.find(query, Favorite.class);
    }

    /**
     * 删除一个喜欢
     * @param favParam
     * @return
     */
    @Override
    public boolean delete(Favorite favParam) {
        if (favParam == null) {
            LOG.error("favParam data is null");
            return false;
        }
        DeleteResult result = mongoTemplate.remove(favParam);
        return result != null && result.getDeletedCount() > 0;
    }
}
