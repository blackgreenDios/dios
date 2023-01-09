package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.services.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;

@Controller(value = "com.blackgreen.dios.controllers.HomeController")
@RequestMapping(value = "/")
public class
HomeController {

    private final RecordService recordService;

    public HomeController(RecordService recordService) {
        this.recordService = recordService;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET)
    public ModelAndView getIndex(@SessionAttribute(value = "user", required = false) UserEntity user) {
        
        ModelAndView modelAndView = new ModelAndView("/home");
        
//        if (user != null) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            String date = formatter.format(this.recordService.getDate(user.getEmail()));
//
//            modelAndView.addObject("date", date);
//        }



        return modelAndView;
    }
}
