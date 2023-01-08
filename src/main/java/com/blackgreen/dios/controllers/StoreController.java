package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.bbs.ArticleEntity;
import com.blackgreen.dios.entities.bbs.ImageEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.record.ElementEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.WriteResult;
import com.blackgreen.dios.services.StoreService;
import com.blackgreen.dios.vos.bbs.CommentVo;
import com.blackgreen.dios.vos.store.CartVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller(value = "com.blackgreen.dios.controllers.StoreController")
@RequestMapping(value = "/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @RequestMapping(value = "list",
            method = RequestMethod.GET)
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView("store/list");
        return modelAndView;
    }


    // cart 페이지
    @RequestMapping(value = "cart",
            method = RequestMethod.GET)
    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("store/cart");
        }

        return modelAndView;
    }

    // cart 요소 불러오기
    @GetMapping(value = "cartItem", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getCartItem(@SessionAttribute(value = "user", required = false) UserEntity user) {

        JSONArray responseArray = new JSONArray();

        CartVo[] carts = this.storeService.getCarts(user);

        for (CartVo cart : carts) {
            JSONObject cartObject = new JSONObject();

            cartObject.put("index", cart.getIndex());
            cartObject.put("userEmail", cart.getUserEmail());
            cartObject.put("count", cart.getCount());
            cartObject.put("itemIndex", cart.getItemIndex());
            cartObject.put("orderColor", cart.getOrderColor());
            cartObject.put("orderSize", cart.getOrderSize());
            cartObject.put("itemName", cart.getItemName());
            cartObject.put("price", cart.getPrice());
            cartObject.put("image", cart.getImage());

            responseArray.put(cartObject);
        }


        return responseArray.toString();
    }

     //cart 요소 불러오기 : 이미지
     @RequestMapping(value = "cartItemImage",
             method = RequestMethod.GET)
     public ResponseEntity<byte[]> getCartItemImage(@SessionAttribute(value = "user", required = false) UserEntity user,
                                                    @RequestParam(value = "id", required = false) int index) {

        CartVo[] carts = this.storeService.getCarts(user);

         if (carts == null) {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         }

         ResponseEntity<byte[]> result = null;

         for (CartVo cart : carts) {

             HttpHeaders headers = new HttpHeaders();
             headers.add("Content-Type", cart.getImageMine());

             result = new ResponseEntity<>(cart.getImage(), headers, HttpStatus.OK);
         }

         return result;
     }

     // cart 선택상품 삭제
     @DeleteMapping(value = "cart",
             produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseBody
     public String deleteCart(@SessionAttribute(value = "user",required = false) UserEntity user,
                              @RequestParam(value = "index") int index){

         Enum<?> result = this.storeService.deleteCart(index);
         JSONObject responseObject = new JSONObject();

         responseObject.put("result",result.name().toLowerCase());

//         if(result == CommonResult.SUCCESS){
//             responseObject.put("index", cart.getIndex());
//         }

         return responseObject.toString();
     }

     // 수량 변경 : 더하기
    @PatchMapping(value = "plusCount",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchPlusCount (@SessionAttribute(value = "user",required = false) UserEntity user,
                               @RequestParam(value = "index") int index) {

        Enum<?> result = this.storeService.updateCountPlus(index);
        JSONObject responseObject = new JSONObject();

        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS){
            responseObject.put("index", index);
        }

        return responseObject.toString();
    }

    // 수량 변경 : 빼기
    @PatchMapping(value = "minusCount",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMinusCount (@SessionAttribute(value = "user",required = false) UserEntity user,
                                  @RequestParam(value = "index") int index) {

        Enum<?> result = this.storeService.updateCountMinus(index);
        JSONObject responseObject = new JSONObject();

        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS){
            responseObject.put("index", index);
        }

        return responseObject.toString();
    }

    // 주문버튼 눌렀을 때 orders 테이블에 상품 정보 insert
    // cart -> order 로 상품정보 넘겨주기
    @PostMapping(value = "order",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postOrder(@SessionAttribute(value = "user", required = false) UserEntity user,
                            OrderEntity order) {
        Enum<?> result;

        JSONObject responseObject = new JSONObject();

        if (user == null) {
            result = CommonResult.FAILURE;
        } else {

            order.setUserEmail(user.getEmail());

            result = this.storeService.addOrder(order);

//            if (result == CommonResult.SUCCESS) {
//                responseObject.put("index", order.getIndex());
//            }
        }

        responseObject.put("result",result.name().toLowerCase());

        return responseObject.toString();
    }



    // order
    @RequestMapping(value = "order",
            method = RequestMethod.GET)
    public ModelAndView getOrder(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("store/order");
        }

        modelAndView.addObject("user", user);

        return modelAndView;
    }

    // order 요소 불러오기
    @GetMapping(value = "orderItem",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getOrderItem(@SessionAttribute(value = "user", required = false) UserEntity user) {

        JSONArray responseArray = new JSONArray();

        OrderVo[] orders = this.storeService.getOrders(user);

        for (OrderVo order : orders) {
            JSONObject orderObject = new JSONObject();

            orderObject.put("index", order.getIndex());
            orderObject.put("userEmail", order.getUserEmail());
            orderObject.put("orderNum", order.getOrderNum());
            orderObject.put("count", order.getCount());
            orderObject.put("itemIndex", order.getItemIndex());
            orderObject.put("orderColor", order.getOrderColor());
            orderObject.put("orderSize", order.getOrderSize());
            orderObject.put("price", order.getPrice());
            orderObject.put("orderStatus", order.getOrderStatus());
            orderObject.put("orderDate", order.getOrderDate());
            orderObject.put("itemName", order.getItemName());
            orderObject.put("image", order.getImage());
            orderObject.put("imageMime", order.getImageMime());
            orderObject.put("statusText", order.getStatusText());

            responseArray.put(orderObject);
        }
        return responseArray.toString();
    }
}
