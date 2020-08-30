package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SubjectSpider {

    private static final String MHZ_URL = "https://fm.douban.com/j/v2/";
    private static final Logger LOG = LoggerFactory.getLogger(SubjectSpider.class);
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    //系统启动时自动执行爬取任务
    //@PostConstruct
    public void init(){
        doExecute();
    }
    //开始执行爬取任务
    public void doExecute(){
        Map channels = getSubjectData();
        getArtistData(channels);
        getMHZData(channels, "scenario");
        getMHZData(channels, "language");
        getMHZData(channels, "genre");
        getMHZData(channels, "artist");
        getCollectionsData();
    }

    /**
     * 执行爬取主题数据
     * @return
     */
    private Map getSubjectData(){
        HttpUtil httpUtil = new HttpUtil();
        String url = MHZ_URL + "rec_channels?specific=all";
        String content = httpUtil.getContent(url, new HashMap<>());
        //LOG.info("爬取成功");
        //反序列化
        Map returnData = JSON.parseObject(content, Map.class);
        Map data = (Map)returnData.get("data");
        Map channels = (Map)data.get("channels");
        return channels;

    }
    private void getArtistData(Map channels){

        List artist = (List)channels.get("artist");
        for (int i = 0; i < artist.size(); i++) {
            Map artistData = (Map)artist.get(i);
            Singer singer = new Singer();
            singer.setId(artistData.get("artist_id").toString());
            singer.setGmtCreated(LocalDateTime.now());
            singer.setGmtModified(LocalDateTime.now());
            singer.setName((String)artistData.get("name"));
            singer.setAvatar((String)artistData.get("cover"));
            List relates = (List)artistData.get("related_artists");
            List<String> similarSingerIds = new ArrayList<>();
            for (int j = 0; j < relates.size(); j++) {
                Map relatesData = (Map)relates.get(j);
                similarSingerIds.add(relatesData.get("id").toString());
            }
            singer.setSimilarSingerIds(similarSingerIds);

            if (singerService.get(singer.getId()) == null) {

                singerService.addSinger(singer);
            }

        }

    }
    private void getMHZData(Map channels, String type){

        List subjects = (List)channels.get(type);
        for (int i = 0; i < subjects.size(); i++) {
            Map subjectData = (Map)subjects.get(i);
            Subject subject = new Subject();
            subject.setId(subjectData.get("id").toString());
            subject.setGmtCreated(LocalDateTime.now());
            subject.setGmtModified(LocalDateTime.now());
            subject.setName((String)subjectData.get("name"));
            subject.setCover((String)subjectData.get("cover"));
            subject.setDescription((String)subjectData.get("intro"));
            subject.setSubjectType(SubjectUtil.TYPE_MHZ);
            if (type.equals("scenario")) {
                subject.setSubjectSubType(SubjectUtil.TYPE_SUB_MOOD);
            } else if (type.equals("genre")) {
                subject.setSubjectSubType(SubjectUtil.TYPE_SUB_STYLE);
            } else if (type.equals("language")) {
                subject.setSubjectSubType(SubjectUtil.TYPE_SUB_AGE);
            } else if (type.equals("artist")){
                subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
                subject.setMaster(subjectData.get("artist_id").toString());
            }
            subject.setPublishedDate(LocalDateTime.now());

            if (subjectService.get(subject.getId()) == null) {

                subjectService.addSubject(subject);
            }

            getSubjectSongData(subject.getId());

        }

    }

    /**
     * 执行爬取主题关联的歌曲数据
     * @param subjectId
     */
    private void getSubjectSongData(String subjectId) {

        HttpUtil httpUtil = new HttpUtil();
        String url = MHZ_URL + "playlist?channel=" + subjectId + "&kbps=128&client=s%3Amainsite%7Cy%3A3.0&app_name=radio_website&version=100&type=n";
        String content = httpUtil.getContent(url, new HashMap<>());
        //LOG.info(content);
        Map returnData = JSON.parseObject(content,Map.class);
        List songDatas = (List)returnData.get("song");

        Subject subject = subjectService.get(subjectId);
        List<String> songIds = new ArrayList<>();

        for (int i = 0; i < songDatas.size(); i++) {

            Map songData = (Map)songDatas.get(i);

            Song song = new Song();
            song.setId(songData.get("sid").toString());
            song.setGmtCreated(LocalDateTime.now());
            song.setGmtModified(LocalDateTime.now());
            song.setName(songData.get("title").toString());
            song.setCover(songData.get("picture").toString());
            song.setUrl(songData.get("url").toString());

            List singersData = (List)songData.get("singers");
            List<String> singerIds =  new ArrayList<>();
            for (int j = 0; j < singersData.size(); j++) {
                Map singerData = (Map)singersData.get(i);
                Singer singer = new Singer();
                singer.setId(singerData.get("id").toString());
                singer.setGmtCreated(LocalDateTime.now());
                singer.setGmtModified(LocalDateTime.now());
                singer.setName(singerData.get("name").toString());
                singer.setAvatar(singerData.get("avatar").toString());
                if (singerService.get(singer.getId()) == null) {
                    singerService.addSinger(singer);
                }

                singerIds.add(singerData.get("id").toString());
            }
            song.setSingerIds(singerIds);
            if (songService.get(song.getId()) == null) {
                songService.add(song);
            }
            songIds.add(songData.get("sid").toString());
        }
        subject.setSongIds(songIds);
        subjectService.modify(subject);

    }

    /**
     * 爬取歌单
     */
    private void getCollectionsData(){

        HttpUtil httpUtil = new HttpUtil();
        String url = "https://douban.fm/j/v2/songlist/explore?type=hot&genre=0&limit=20&sample_cnt=5";

        String content = httpUtil.getContent(url, new HashMap<>());

        List<Map> returnData = JSON.parseObject(content,List.class);
        for (int i = 0; i < returnData.size(); i++) {
            Map subjectData = (Map) returnData.get(i);
            Subject subject = new Subject();
            subject.setId(subjectData.get("id").toString());
            subject.setName(subjectData.get("title").toString());
            subject.setCover(subjectData.get("cover").toString());
            subject.setGmtCreated(LocalDateTime.now());
            subject.setGmtModified(LocalDateTime.now());

            subject.setPublishedDate(stringToDate(subjectData.get("created_time").toString()));
            subject.setDescription(subjectData.get("intro").toString());
            subject.setSubjectType(SubjectUtil.TYPE_COLLECTION);
            Map creator = (Map)subjectData.get("creator");
            subject.setMaster(creator.get("id").toString());
            List simpleSongs = (List)subjectData.get("sample_songs");
            List<String> songIds = new ArrayList<>();
            for (int j = 0; j < simpleSongs.size(); j++) {
                Map songData = (Map)simpleSongs.get(j);
                Song song = new Song();
                String id = songData.get("sid").toString();
                song.setId(id);
                songIds.add(id);
                song.setName(songData.get("title").toString());
                if (songService.get(song.getId()) == null) {
                    songService.add(song);
                }

            }
            subject.setSongIds(songIds);
            if (subjectService.get(subject.getId()) == null) {

                subjectService.addSubject(subject);
            }
            subjectService.modify(subject);
            Singer singer = new Singer();
            singer.setId(creator.get("id").toString());
            singer.setGmtCreated(LocalDateTime.now());
            singer.setGmtModified(LocalDateTime.now());
            singer.setName(creator.get("name").toString());
            singer.setAvatar(creator.get("picture").toString());
            singer.setHomePage(creator.get("url").toString());
            if (singerService.get(singer.getId()) == null) {
                singerService.addSinger(singer);

            }
        }

    }

    private LocalDateTime stringToDate(String date){

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, df);
    }



}
