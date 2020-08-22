package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
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
public class SongServiceImpl implements SongService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(SongServiceImpl.class);

    /**
     *  增加一首歌曲
     * @param song
     * @return
     */
    @Override
    public Song add(Song song) {
        if (song == null) {
            LOG.error("Song data is null");
            return null;
        }

        return mongoTemplate.insert(song);
    }
    /**
     * 根据id查询
     * @param songId
     * @return
     */
    @Override
    public Song get(String songId) {
        if (!StringUtils.hasText(songId)) {
            LOG.error("songId is empty");
            return null;
        }
        return mongoTemplate.findById(songId,Song.class);
    }
    /**
     * 分页查询全部歌曲
     * @param songParam
     * @return
     */
    @Override
    public Page<Song> list(SongQueryParam songParam) {
        if (songParam == null) {
            LOG.error("input song data is not correct.");
            return null;
        }

        // 查询总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();

        if (StringUtils.hasText(songParam.getName())) {
            subCris.add(Criteria.where("name").is(songParam.getName()));
        }

        if (StringUtils.hasText(songParam.getId())) {
            subCris.add(Criteria.where("id").is(songParam.getId()));
        }

        if (subCris.isEmpty()) {
            Pageable pageable = PageRequest.of(songParam.getPageNum()-1,songParam.getPageSize());
            Query query = new Query();
            query.with(pageable);
            List<Song> songs = mongoTemplate.find(query, Song.class);
            long count = mongoTemplate.count(query, Song.class);
            Page<Song> pageResult = PageableExecutionUtils.getPage(songs,pageable,new LongSupplier(){
                @Override
                public long getAsLong() {
                    return count;
                }
            });

            return pageResult;
        }

        // 三个子条件以 and 关键词连接成总条件对象，相当于 name='' and lyrics='' and subjectId=''
        criteria.andOperator(subCris.toArray(new Criteria[]{}));

        // 条件对象构建查询对象
        Query query = new Query(criteria);
        Pageable pageable = PageRequest.of(songParam.getPageNum()-1,songParam.getPageSize());

        query.with(pageable);
        List<Song> songs = mongoTemplate.find(query, Song.class);
        long count = mongoTemplate.count(query, Song.class);
        Page<Song> pageResult = PageableExecutionUtils.getPage(songs,pageable,new LongSupplier(){
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;

    }
    /**
     * 修改一首歌曲
     * @param song
     * @return
     */
    @Override
    public boolean modify(Song song) {
        if (song == null || !StringUtils.hasText(song.getId())){
            LOG.error("Song data or id is null");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(song.getId()));
        Update update = new Update();
        if (song.getCover() != null){
            update.set("cover",song.getCover());
        }
        if (song.getGmtModified() != null){
            update.set("gmtModified",song.getGmtModified());
        }
        if (song.getName() != null){
            update.set("name",song.getName());
        }
        if (song.getLyrics() != null){
            update.set("lyrics",song.getLyrics());
        }
        if (song.getUrl() != null){
            update.set("url",song.getUrl());
        }
        if (song.getSingerIds() != null){
            update.set("singerIds",song.getSingerIds());
        }
        UpdateResult result = mongoTemplate.updateFirst(query,update,Song.class);
        return result != null && result.getModifiedCount() > 0;
    }
    /**
     * 删除一首歌曲
     * @param songId
     * @return
     */
    @Override
    public boolean delete(String songId) {
        if (!StringUtils.hasText(songId)) {
            LOG.error("id is null");
            return false;
        }
        Song song = new Song();
        song.setId(songId);
        DeleteResult result = mongoTemplate.remove(song);

        return result != null && result.getDeletedCount() > 0;
    }
}
