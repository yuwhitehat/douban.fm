package fm.douban.service;

import fm.douban.model.Subject;

import java.util.List;

public interface SubjectService {
    /**
     * 增加一个主题
     * @param subject
     * @return
     */
    public Subject addSubject(Subject subject);

    /**
     * 查询单个主题
     * @param subjectId
     * @return
     */
    public Subject get(String subjectId);

    /**
     * 查询一组主题
     * @param type
     * @return
     */
    public List<Subject> getSubjects(String type);

    /**
     * 查询一组主题
     * @param type
     * @param subType
     * @return
     */
    public List<Subject> getSubjects(String type, String subType);

    /**
     * 删除一组主题
     * @param subjectId
     * @return
     */
    public boolean delete(String subjectId);

    /**
     * 修改主题
     * @param subject
     * @return
     */
    public boolean modify(Subject subject);
}
