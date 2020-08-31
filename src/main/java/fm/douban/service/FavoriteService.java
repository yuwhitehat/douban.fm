package fm.douban.service;

import fm.douban.model.Favorite;

import java.util.List;

public interface FavoriteService {


    /**
     * 新增一个喜欢
     * @param fav
     * @return
     */
    public Favorite add(Favorite fav);

    /**
     * 计算喜欢数，如果数大于0，表示已经喜欢
     * @param favParam
     * @return
     */
    public List<Favorite> list(Favorite favParam);

    /**
     * 删除一个喜欢
     * @param favParam
     * @return
     */
    public boolean delete(Favorite favParam);
}
