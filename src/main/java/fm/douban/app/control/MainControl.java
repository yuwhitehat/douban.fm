package fm.douban.app.control;

import com.alibaba.fastjson.JSON;
import fm.douban.model.*;
import fm.douban.param.SongQueryParam;
import fm.douban.service.FavoriteService;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.FavoriteUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainControl {

    private static final Logger LOG = LoggerFactory.getLogger(MainControl.class);

    @Autowired
    private SongService songService;
    @Autowired
    private SingerService singerService;
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 首页
     * @param model
     * @return
     */
    @GetMapping(path = "/index")
    public String index(Model model) {
        //设置首屏歌曲数据
        setSongData(model);

        //mhz
        setMhzData(model);

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

    /**
     * 我的页面
     * @param model
     * @param request
     * @param response
     * @return
     */
    @GetMapping(path = "/my")
    public String myPage(Model model, HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(FavoriteUtil.TYPE_RED_HEART);
        List<Favorite> favorites = favoriteService.list(favorite);
        model.addAttribute("favorites",favorites);

        List<Song> songs = new ArrayList<>();
        if (favorites != null && !favorites.isEmpty()) {
            for (Favorite favorite1 : favorites) {
                if (FavoriteUtil.TYPE_RED_HEART.equals(favorite1.getType()) && FavoriteUtil.ITEM_TYPE_SONG.equals(favorite.getItemType())) {
                    Song song = songService.get(favorite1.getItemId());
                    if (song != null) {
                        songs.add(song);
                    }

                }
            }
        }

        model.addAttribute("songs", songs);
        return "my";
    }

    /**
     * 喜欢或者不喜欢操作，对前端比较简单，不必判断状态
     * 已经喜欢，则删除，表示执行不喜欢操作
     * 还没有喜欢记录，则新增，表示执行喜欢操作
     * @param itemType
     * @param itemId
     * @param request
     * @param response
     * @return
     */
    @GetMapping(path = "/fav")
    @ResponseBody
    public Map doFav(@RequestParam(name = "itemType") String itemType,@RequestParam(name = "itemId") String itemId ,
                     HttpServletRequest request, HttpServletResponse response) {

        Map returnData = new HashMap();
        HttpSession session = request.getSession();
        UserLoginInfo userLoginInfo = (UserLoginInfo)session.getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(FavoriteUtil.TYPE_RED_HEART);
        favorite.setItemType(itemType);
        favorite.setItemId(itemId);

        List<Favorite> favorites = favoriteService.list(favorite);

        if (favorites == null || favorites.isEmpty()) {
            favoriteService.add(favorite);
        } else {
            favoriteService.delete(favorite);
        }

        returnData.put("message", "successful");
        return returnData;
    }
    @GetMapping(path = "/share")
    public String share(Model model){
        return "share";
    }

    @GetMapping(path = "/error")
    public String error(Model model) {
        return "error";
    }

    /**
     * 设置首屏歌曲数据
     * @param model
     */
    private void setSongData(Model model){
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

    }

    /**
     * 设置mhz数据
     * @param model
     */
    private void setMhzData(Model model) {

        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        //在内存中分类，避免查询四次
        List<Subject> ages = new ArrayList<>();
        List<Subject> moods = new ArrayList<>();
        List<Subject> styles = new ArrayList<>();
        List<Subject> artists = new ArrayList<>();
        if (subjects == null && !subjects.isEmpty()) {
            for (Subject subject : subjects) {
                if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)) {
                    ages.add(subject);
                }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD)) {
                    moods.add(subject);
                } else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE)){
                    styles.add(subject);
                }else if (subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)) {
                    artists.add(subject);
                } else {
                    LOG.error("subject data error. unknown subtype. subject=" + JSON.toJSONString(subject));
                }
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

    }
}
