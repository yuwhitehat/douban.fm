package fm.douban.service;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import org.springframework.data.domain.Page;

public interface SongService {
    /**
     * 增加一首歌曲
     * @param song
     * @return
     */
    public Song add(Song song);
    /**
     * 根据id查询
     * @param songId
     * @return
     */
    public Song get(String songId);
    /**
     * 查询全部歌曲
     * @param songParam
     * @return
     */
    public Page<Song> list(SongQueryParam songParam);
    /**
     * 修改一首歌曲
     * @param song
     * @return
     */
    public boolean modify(Song song);
    /**
     * 删除一首歌曲
     * @param songId
     * @return
     */
    public boolean delete(String songId);
}
