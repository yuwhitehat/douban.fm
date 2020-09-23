package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;

import fm.douban.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class SingerSongSpider {

    private static final Logger LOG = LoggerFactory.getLogger(SingerSongSpider.class);
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;
    @PostConstruct
    public void init(){
        CompletableFuture.supplyAsync(() -> doExecute())
                .thenAccept(result -> LOG.info("spider end ..."));

    }
    //开始执行爬取任务
    public boolean doExecute(){

        getSongDataBySingers();
        return true;
    }

    /**
     * 执行爬取歌曲数据
     */
    private void getSongDataBySingers(){
        List<Singer> singers = singerService.getAll();
        singers.forEach(singer -> {
            HttpUtil httpUtil = new HttpUtil();
            String url = "https://fm.douban.com/j/v2/artist/" + singer.getId() + "/";
            String content = httpUtil.getContent(url, new HashMap<>());
            LOG.info("爬取数据成功");
            Map returnData = JSON.parseObject(content, Map.class);

            getSongList(returnData);
            getSimilarSingers(singer, returnData);
        });
    }


    /**
     * 解析相似歌手
     * @param singer
     * @param returnData
     */
    private void getSimilarSingers(Singer singer, Map returnData) {

        Map relatedChannel = (Map)returnData.get("related_channel");
        List<Map> similarSingers = (List<Map>)relatedChannel.get("similar_artists");
        List<String> similarSingerIds = new ArrayList<>();
        similarSingers.forEach(similarSingersData -> {
            Singer singer2 = new Singer();
            String id = similarSingersData.get("id").toString();
            singer2.setId(id);
            singer2.setName(similarSingersData.get("name").toString());
            singer2.setAvatar(similarSingersData.get("avatar").toString());
            if (singerService.get(singer.getId()) == null) {
                singerService.addSinger(singer);
            }
            similarSingerIds.add(id);
        });

        singer.setSimilarSingerIds(similarSingerIds);

        singerService.modify(singer);
    }

    /**
     * 解析歌曲列表
     * @param returnData
     */
    private void getSongList(Map returnData) {

        Map songList = (Map)returnData.get("songlist");
        List<Map> songs = (List<Map>)songList.get("songs");
        songs.forEach(songData -> {
            Song song  = new Song();
            song.setId(songData.get("sid").toString());
            song.setName(songData.get("title").toString());
            song.setCover(songData.get("picture").toString());
            song.setUrl(songData.get("url").toString());
            song.setGmtCreated(LocalDateTime.now());
            song.setGmtModified(LocalDateTime.now());
            List singers1 = (List)songData.get("singers");
            List<String> singerIds = new ArrayList<>();
            for (int j = 0; j < singers1.size(); j++) {
                Map singer1 = (Map)singers1.get(j);
                singerIds.add(singer1.get("id").toString());
            }
            song.setSingerIds(singerIds);
            if (songService.get(song.getId()) == null) {
                songService.add(song);
            }
            songService.modify(song);
        });

    }
}
