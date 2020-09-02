package fm.douban.app.control;

import fm.douban.model.CollectionViewModel;
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
        if (subject == null) {
            return "error";
        }
        //传递subject给前端
        model.addAttribute("subject", subject);
        //传递songs给前端
        List<String> ids = subject.getSongIds();
        List<Song> songs = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (String id : ids) {
                Song song = songService.get(id);
                if (song != null) {
                    songs.add(song);
                }
            }
        }
        model.addAttribute("songs",songs);

        Singer singer = singerService.get(subject.getMaster());
        model.addAttribute("singer", singer);
        List<String> similarSingerIds = singer.getSimilarSingerIds();
        List<Singer> singers = new ArrayList<>();
        if (similarSingerIds != null && !similarSingerIds.isEmpty()) {
            for (String id : similarSingerIds) {
                Singer simSinger = singerService.get(id);
                if (simSinger != null) {
                    singers.add(simSinger);
                }
            }
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

        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_COLLECTION);

        List<List<CollectionViewModel>> subjectColumns = new ArrayList<>();
        //最大行数
        int lineCount = (subjects.size() %5 == 0) ? subjects.size() / 5 : (subjects.size() / 5 + 1);
        //列数 最多5列
        for (int i = 0; i < 5; i++) {
            //每列的元素
            List<CollectionViewModel> column = new ArrayList<>();
            //第一列的元素是 0 5 11
            for (int j = 0; j < lineCount; j++) {
                int itemIndex = i + j * 5;
                if (itemIndex < subjects.size()) {
                    Subject subject = subjects.get(itemIndex);
                    CollectionViewModel xvm = new CollectionViewModel();
                    xvm.setSubject(subject);

                    if (subject.getMaster() != null) {
                        Singer singer = singerService.get(subject.getMaster());
                        xvm.setSinger(singer);
                    }

                    if (subject.getSongIds() != null && !subject.getSongIds().isEmpty()) {
                        List<Song> songs = new ArrayList<>();
                        subject.getSongIds().forEach(songId -> {
                            Song song = songService.get(songId);
                            songs.add(song);
                        });
                        xvm.setSongs(songs);
                    }

                    column.add(xvm);
                }
            }
            subjectColumns.add(column);
        }
        model.addAttribute("subjectColumns", subjectColumns);
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
