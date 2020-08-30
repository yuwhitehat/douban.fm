package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SubjectControl {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SongService songService;

    @Autowired
    private SingerService singerService;

    /**
     * 从艺术家出发详情页
     * @param model
     * @param subjectId
     * @return
     */
    @GetMapping(path = "/artist")
    public String mhzDetail(Model model, @RequestParam(name = "subjectId") String subjectId) {
        Subject subject = subjectService.get(subjectId);
        //传递subject给前端
        model.addAttribute("subject", subject);
        //传递songs给前端
        List<String> ids = subject.getSongIds();
        List<Song> songs = new ArrayList<>();
        for (String id : ids) {
            Song song = songService.get(id);
            songs.add(song);
        }
        model.addAttribute("songs",songs);

        Singer singer = singerService.get(subject.getMaster());
        model.addAttribute("singer", singer);
        List<String> similarSingerIds = singer.getSimilarSingerIds();
        List<Singer> singers = new ArrayList<>();
        for (String id : similarSingerIds) {
            Singer simSingers = singerService.get(id);
            singers.add(simSingers);
        }
        model.addAttribute("simSingers",singers);
        return "mhzdetail";
    }

    /**
     * 歌单
     * @param model
     * @return
     */
    @GetMapping(path = "/collection")
    public String collection(Model model) {
        Subject sub = new Subject();
        sub.setSubjectType(SubjectUtil.TYPE_COLLECTION);
        List<Subject> subjects = subjectService.getSubjects(sub);
        model.addAttribute("subjects", subjects);
        List<Song> songs = new ArrayList<>();
        for (Subject subject : subjects) {

            Singer singer = singerService.get(subject.getMaster());
            model.addAttribute("singer",singer);
            List<String> songIds = subject.getSongIds();

            for (String id : songIds) {
                Song song = songService.get(id);
                songs.add(song);
            }

        }
        model.addAttribute("songs",songs);
        return "collection";
    }

    /**
     * 歌单详情页
     * @param model
     * @param subjectId
     * @return
     */
    @GetMapping(path = "/collectiondetail")
    public String collectionDetail(Model model, @RequestParam(name = "subjectId") String subjectId){
        Subject subject = subjectService.get(subjectId);
        model.addAttribute("subject",subject);
        Singer singer = singerService.get(subject.getMaster());
        model.addAttribute("singer",singer);

        List<Song> songs = new ArrayList<>();
        for (String id : subject.getSongIds()) {
            Song song = songService.get(id);
            songs.add(song);
        }
        model.addAttribute("songs", songs);

        List<Subject> otherSubjects = subjectService.getSubjects(subject);
        model.addAttribute("otherSubjects", otherSubjects);
        return "collectiondetail";
    }

}
