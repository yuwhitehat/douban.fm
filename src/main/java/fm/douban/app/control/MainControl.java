package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainControl {

    @Autowired
    private SongService songService;
    @Autowired
    private SingerService singerService;

    @GetMapping(path = "/index")
    public String index(Model model) {

        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        //分页查询这里，要怎么取得Song这个记录呢
        Page<Song> pageSongs = songService.list(songParam);
        Song song = null;
        if (pageSongs != null && pageSongs.getContent() != null && pageSongs.getContent().size() > 0) {
            song = pageSongs.getContent().get(0);
        }

        model.addAttribute("song", song);
        List<String> ids = song.getSingerIds();
        List<Singer> singers = new ArrayList<>();
        for (String id : ids) {

            singers.add(singerService.get(id));
        }
        model.addAttribute("singers",singers);
        return "index";
    }
}
