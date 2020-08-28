package fm.douban.service;

import fm.douban.model.Singer;

import java.util.List;

public interface SingerService {
    /**
     * 增加一个歌手
     * @param singer
     * @return
     */
    public Singer addSinger(Singer singer);

    /**
     *根据歌手id查询歌手
     * @param singerId
     * @return
     */
    public Singer get(String singerId);

    /**
     * 查询全部歌手
     * @return
     */
    public List<Singer> getAll();

    /**
     * 修改歌手，只能修改名称、头像、主页、相似的歌手id
     * @param singer
     * @return
     */
    public boolean modify(Singer singer);

    /**
     * 根据id删除歌手
     * @param singerId
     * @return
     */
    public boolean delete(String singerId);

    /**
     * 删除所有数据
     * @return
     */
    public boolean deleteAll();
}
