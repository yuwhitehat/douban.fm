package fm.douban.app.control;

import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SubjectTestControl {

    @Autowired
    private SubjectService subjectService;

    @GetMapping(path = "/test/subject/add")
    public Subject testAdd(){
        Subject subject = new Subject();
        subject.setId("0");
        subject.setGmtCreated(LocalDateTime.now());
        subject.setGmtModified(LocalDateTime.now());

        subject.setName("运动");
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
        subject.setMaster("BY2");
        return subjectService.addSubject(subject);
    }
    @GetMapping(path = "/test/subject/get")
    public Subject testGet(){
        return subjectService.get("0");

    }
    @GetMapping(path = "/test/subject/getByType")
    public List<Subject> testGetByType(){
        Subject subject = new Subject();
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        return subjectService.getSubjects(subject.getSubjectType());
    }
    @GetMapping(path = "/test/subject/getBySubType")
    public List<Subject> testGetBySubType(){

        Subject subject = new Subject();
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
        return subjectService.getSubjects(subject.getSubjectType(),subject.getSubjectSubType());

    }
    @GetMapping(path = "/test/subject/del")
    public boolean testDelete(){

       return subjectService.delete("0");

    }
    @GetMapping(path = "/test/subject/delAll")
    public boolean testDeleteAll(){
        return subjectService.deleteAll();
    }

}
