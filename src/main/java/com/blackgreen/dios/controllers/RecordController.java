package com.blackgreen.dios.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "com.blackgreen.dios.controllers.RecordController")
@RequestMapping(value = "/record")
public class RecordController {
    @RequestMapping(value = "goal",
            method = RequestMethod.GET)
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView("records/goal");
        return modelAndView;
    }

}
