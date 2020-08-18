package fm.douban.app.control;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SongTestControl {
    @Autowired
    private SongService songService;

    @GetMapping(path = "/test/song/add")
    public Song testAdd(){
        Song song = new Song();
        List<String> singers = new ArrayList<>();
        singers.add("伍佰");
        song.setId("0");
        song.setGmtCreated(LocalDateTime.now());
        song.setGmtModified(LocalDateTime.now());
        song.setCover("http://cover");
        song.setName("last dance");
        song.setLyrics("所以暂时将你眼睛闭了起来");
        song.setUrl("http://url");
        song.setSingerIds(singers);
        return songService.add(song);
    }
    @GetMapping(path = "/test/song/get")
    public Song testGet(){
        return songService.get("0");
    }
    @GetMapping(path = "/test/song/list")
    public Page<Song> testList(){
        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        return songService.list(songParam);
    }
    @GetMapping(path = "/test/song/modify")
    public boolean testModify(){
        Song song = new Song();
        song.setId("0");
        song.setName("mama");
        return songService.modify(song);
    }
    @GetMapping(path = "/test/song/del")
    public boolean testDelete(){
        Song song = new Song();
        song.setId("0");
        return songService.delete(song.getId());
    }
}
