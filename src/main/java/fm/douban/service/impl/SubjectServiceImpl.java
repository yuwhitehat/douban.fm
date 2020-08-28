package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(SubjectServiceImpl.class);
    /**
     * 增加一个主题
     * @param subject
     * @return
     */
    @Override
    public Subject addSubject(Subject subject) {
        if(subject == null) {
            LOG.error("subject data is null");
            return null;
        }
        return mongoTemplate.insert(subject);
    }
    /**
     * 查询单个主题
     * @param subjectId
     * @return
     */
    @Override
    public Subject get(String subjectId) {
        if (!StringUtils.hasText(subjectId)) {
            LOG.error("id is null");
            return null;
        }
        return mongoTemplate.findById(subjectId, Subject.class);
    }
    /**
     * 查询一组主题
     * @param type
     * @return
     */
    @Override
    public List<Subject> getSubjects(String type) {
        if (!StringUtils.hasText(type)) {
            LOG.error("type is null");
            return null;
        }
        Query query = new Query(Criteria.where("subjectType").is(type));

        return mongoTemplate.find(query, Subject.class);
    }
    /**
     * 查询一组主题
     * @param type
     * @param subType
     * @return
     */
    @Override
    public List<Subject> getSubjects(String type, String subType) {
        if (!StringUtils.hasText(type) || !StringUtils.hasText(subType)) {
            LOG.error("type or subType is null");
            return null;
        }

        Criteria criteria = new Criteria();
        // 多个子条件
        List<Criteria> subCris = new ArrayList();
        subCris.add(Criteria.where("subjectType").is(type));
        subCris.add(Criteria.where("subjectSubType").is(subType));
        criteria.andOperator(subCris.toArray(new Criteria[]{}));
        Query query = new Query(criteria);

        return mongoTemplate.find(query, Subject.class);
    }
    /**
     * 删除一组主题
     * @param subjectId
     * @return
     */
    @Override
    public boolean delete(String subjectId) {

        if (!StringUtils.hasText(subjectId)) {
            LOG.error("id is null");
            return false;
        }
        Subject subject = new Subject();
        subject.setId(subjectId);
        DeleteResult result = mongoTemplate.remove(subject);

        return result != null && result.getDeletedCount() > 0;
    }
    /**
     * 修改主题
     * @param subject
     * @return
     */
    @Override
    public boolean modify(Subject subject) {
        if (subject == null || !StringUtils.hasText(subject.getId())) {
            LOG.error("subject data or id is null");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(subject.getId()));
        Update update = new Update();

        if (subject.getSongIds() != null) {
            update.set("songIds", subject.getSongIds());
        }
        if (subject.getMaster() != null) {
            update.set("master",subject.getMaster());
        }

        UpdateResult result = mongoTemplate.updateFirst(query, update, Subject.class);
        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAll() {
        DeleteResult result = mongoTemplate.remove(new Query(), Subject.class);
        return result != null && result.getDeletedCount() > 0;
    }
}
