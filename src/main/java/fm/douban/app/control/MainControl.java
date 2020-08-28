package fm.douban.app.control;

import fm.douban.model.MhzViewModel;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainControl {

    @Autowired
    private SongService songService;
    @Autowired
    private SingerService singerService;
    @Autowired
    private SubjectService subjectService;

    /**
     * 首页
     * @param model
     * @return
     */
    @GetMapping(path = "/index")
    public String index(Model model) {

        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        //分页查询这里，要取得Song这个记录
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

        //mhz
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        List<Subject> ages = new ArrayList<>();
        List<Subject> moods = new ArrayList<>();
        List<Subject> styles = new ArrayList<>();
        List<Subject> artists = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)) {
                ages.add(subject);
            }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD)) {
                moods.add(subject);
            } else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE)){
                styles.add(subject);
            }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)) {
                artists.add(subject);
            }
        }
        model.addAttribute("artistDatas", artists);

        //三个区块数据组装
        MhzViewModel mhzViewModel = new MhzViewModel("心情 / 场景",moods);
        MhzViewModel mhzViewModel2 = new MhzViewModel("语言 / 年代",ages);
        MhzViewModel mhzViewModel3 = new MhzViewModel("风格 / 流派",styles);
        List<MhzViewModel> mhzViewModels = new ArrayList<>();
        mhzViewModels.add(mhzViewModel);
        mhzViewModels.add(mhzViewModel2);
        mhzViewModels.add(mhzViewModel3);
        model.addAttribute("mhzViewModels",mhzViewModels);
        return "index";
    }

    /**
     * 搜索页
     * @param model
     * @return
     */
    @GetMapping(path = "/search")
    public String search(Model model) {

        return "search";
    }

    /**
     * 搜索结果
     * @param keyword
     * @return
     */
    @GetMapping(path = "/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam(name = "keyword") String keyword) {
        SongQueryParam songQueryParam = new SongQueryParam();
        songQueryParam.setPageSize(10);
        songQueryParam.setPageNum(1);
        songQueryParam.setName(keyword);
        Page<Song> pageSongs = songService.list(songQueryParam);
        List<Song> songs = new ArrayList<>();

        if (pageSongs != null && pageSongs.getContent() != null && pageSongs.getContent().size() > 0) {
            for (Song song : pageSongs.getContent()) {
                songs.add(song);
            }
        }

        Map<String, List<Song>> map = new HashMap<>();
        map.put("songs",songs);
        return map;
    }
}
