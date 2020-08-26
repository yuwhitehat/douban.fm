package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

}
