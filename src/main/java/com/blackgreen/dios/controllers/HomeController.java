package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.bbs.ArticleEntity;
import com.blackgreen.dios.entities.bbs.BoardEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.BbsService;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "com.blackgreen.dios.controllers.HomeController")
@RequestMapping(value = "/")

public class HomeController {

    private final BbsService bbsService;

    @Autowired
    public HomeController(BbsService bbsService) {

        this.bbsService = bbsService;

    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(){


        ModelAndView modelAndView=new ModelAndView("home/index");
        BoardEntity board1=this.bbsService.getBoard("notice");
        BoardEntity board2=this.bbsService.getBoard("free");

        ArticleEntity article = new ArticleEntity();
        modelAndView.addObject("board1",board1);
        modelAndView.addObject("board2",board2);




        ArticleReadVo[] articles=this.bbsService.getArticleByNotice(article);
            modelAndView.addObject("articles",articles);
        ArticleReadVo[] best=this.bbsService.getArticleByBest(article);
        modelAndView.addObject("article",best);



        return modelAndView;
    }

}
