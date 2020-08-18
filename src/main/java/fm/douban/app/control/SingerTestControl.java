package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SingerTestControl {

    @Autowired
    private SingerService singerService;

     @GetMapping(path = "/test/singer/add")
     public Singer testAddSinger(){

         Singer singer = new Singer();
         singer.setId("0");
         singer.setGmtCreated(LocalDateTime.now());
         singer.setGmtModified(LocalDateTime.now());
         singer.setName("lisa");
         singer.setAvatar("http://youkeda/vdf");
         singer.setHomePage("homePage");

         singerService.addSinger(singer);
         return singer;
     }
     @GetMapping(path = "/test/singer/getAll")
     public List<Singer> testGetAll(){

         return singerService.getAll();
     }
     @GetMapping(path = "/test/singer/getOne")
     public Singer testGetSinger(){

         return singerService.get("0");
     }
     @GetMapping(path = "/test/singer/modify")
     public boolean testModifySinger(){
         Singer singer = new Singer();
         singer.setId("0");
         singer.setName("beyond");
         return singerService.modify(singer);
     }
     @GetMapping(path = "/test/singer/del")
     public boolean testDelSinger(){
         Singer singer = new Singer();
         singer.setId("0");
         return singerService.delete(singer.getId());
     }

}
