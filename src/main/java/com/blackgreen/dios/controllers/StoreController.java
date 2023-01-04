package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.services.GoodsService;
import com.blackgreen.dios.vos.GoodsVo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "com.blackgreen.dios.controllers.StoreController")
@RequestMapping(value = "/store")
public class StoreController {

    private final GoodsService goodsService;

    public StoreController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }


    @RequestMapping(value = "list",
            method = RequestMethod.GET)
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView("store/list");
        return modelAndView;
    }

    @RequestMapping(value = "order",
            method = RequestMethod.GET)
    public ModelAndView getOrder(@SessionAttribute(value = "user",  required = false) UserEntity user) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("store/order");
        }

        modelAndView.addObject("user", user);

        return modelAndView;
    }


    // 코코야가 하는 cart
    @RequestMapping(value = "cart", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart() {
        ModelAndView modelAndView = new ModelAndView("store/cart");
        return modelAndView;
    }

}
