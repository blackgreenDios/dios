package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.store.ItemCategoryEntity;
import com.blackgreen.dios.entities.store.SellerEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "com.blackgreen.dios.controllers.HomeController")
@RequestMapping(value = "/dios")
public class HomeController {

    @RequestMapping(value = "home",
            method = RequestMethod.GET)
    public ModelAndView getHome() {
        ModelAndView modelAndView = new ModelAndView("/home");

        return modelAndView;
    }
}
