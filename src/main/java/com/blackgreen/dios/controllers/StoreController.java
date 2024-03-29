package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.GoodsService;
import com.blackgreen.dios.services.StoreService;
import com.blackgreen.dios.vos.goods.GoodsVo;
import com.blackgreen.dios.vos.store.CartVo;
import com.blackgreen.dios.vos.store.OrderVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import static java.lang.Integer.parseInt;

@Controller(value = "com.blackgreen.dios.controllers.StoreController")
@RequestMapping(value = "/store")
public class StoreController {

    private final StoreService storeService;
    private final GoodsService goodsService;


    public StoreController(StoreService storeService, GoodsService goodsService) {
        this.storeService = storeService;
        this.goodsService = goodsService;
    }

    @GetMapping(value = "list")
    public ModelAndView getList(@RequestParam(value = "cad", required = false) String categoryId,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {

        page = Math.max(1, page);
        ModelAndView modelAndView = new ModelAndView("store/list");


        ItemCategoryEntity[] categories = this.goodsService.getItemCategory();
        int totalCount; //pagination 하려고 가져온 것
//        ItemCategoryEntity category = this.goodsService.getCategory(categoryId);


        totalCount = this.goodsService.getItemCount(categoryId);


        PagingModel paging = new PagingModel(6, totalCount, page);
        modelAndView.addObject("paging", paging);

        // categoryId 도 매개변수로 받는 이유는 카테고리 누르면 카테고리별로 분류된 상품 보려고
        GoodsVo[] goods = this.goodsService.getItems(paging, categoryId);

        ItemEntity[] goodsImage = this.goodsService.getItemImages();
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("goodsImage", goodsImage);
        modelAndView.addObject("category", categories);
        modelAndView.addObject("cad",categoryId);

        for (GoodsVo item : goods) {
            ReviewEntity[] reviews = this.goodsService.getReviews(item.getIndex());
            int sum = 0;
            int reviewCount = reviews.length;
            double ScoreAvg = 0;

            for (ReviewEntity review : reviews) {
                sum += review.getScore();
            }
            if (reviewCount > 0) {
                ScoreAvg = (double) sum / reviewCount;
                ScoreAvg = (double) (Math.round(ScoreAvg * 10));
                ScoreAvg = ScoreAvg / 10;
            } else {
                ScoreAvg = 0;
            }
            item.setReviewCount(this.goodsService.getReviewCount(item.getIndex()));
            item.setScoreAvg(ScoreAvg);
        }


        Random random = new Random();
        int[] items = this.goodsService.getIndex();
        int num1 = items[random.nextInt(items.length - 1)];
        int num2 = items[random.nextInt(items.length - 1)];
        int num3 = items[random.nextInt(items.length - 1)];
        int num4 = items[random.nextInt(items.length - 1)];
        int num5 = items[random.nextInt(items.length - 1)];
        int num6 = items[random.nextInt(items.length - 1)];
        int num7 = items[random.nextInt(items.length - 1)];
        int num8 = items[random.nextInt(items.length - 1)];
        modelAndView.addObject("random1", num1);
        modelAndView.addObject("random2", num2);
        modelAndView.addObject("random3", num3);
        modelAndView.addObject("random4", num4);
        modelAndView.addObject("random5", num5);
        modelAndView.addObject("random6", num6);
        modelAndView.addObject("random7", num7);
        modelAndView.addObject("random8", num8);

//
//        for (GoodsVo item : goods) {
//            SellerEntity seller = this.goodsService.getBrand(item.getSellerIndex());
//            modelAndView.addObject("seller", seller);
//        }


        return modelAndView;
    }

    @GetMapping(value = "titleImage") // 다운로드용 맵핑
    public ResponseEntity<byte[]> getTitleImage(@RequestParam(value = "index") int index) {
        ItemEntity image = this.goodsService.getItemTitleImage(index);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(image.getTitleImageData().length);
        headers.add("Content-Type", image.getTitleImageMime()); // header의 contentType이 아~주 중요해요 ~!, 이해는 하되,, 외우진 마라
        // 프젝하다가 업로드, 다운로드 할 일이 있는데 이걸 교과서처럼 참고하삼요~!!!
        return new ResponseEntity<>(image.getTitleImageData(), headers, HttpStatus.OK);
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


    // cart 선택상품 삭제
    @DeleteMapping(value = "cart",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteCart(@SessionAttribute(value = "user", required = false) UserEntity user,
                             @RequestParam(value = "index") String indexStr) throws JsonProcessingException {

        CartEntity[] carts = new ObjectMapper().readValue(indexStr, CartEntity[].class);


        Enum<?> result = this.storeService.deleteCart(user, carts);
        JSONObject responseObject = new JSONObject();

        responseObject.put("result", result.name().toLowerCase());

//         if(result == CommonResult.SUCCESS){
//             responseObject.put("index", cart.getIndex());
//         }

        return responseObject.toString();
    }

    // 수량 변경 : 더하기
    @PatchMapping(value = "plusCount",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchPlusCount(@SessionAttribute(value = "user", required = false) UserEntity user,
                                 @RequestParam(value = "index") int index) {
        JSONObject responseObject = new JSONObject();
        responseObject.put("count", this.storeService.updateCountPlus(user, index));
        responseObject.put("price", this.storeService.getCartItemPrice(user, index));
        return responseObject.toString();
    }

    // 수량 변경 : 빼기
    @PatchMapping(value = "minusCount",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMinusCount(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "index") int index) {

        JSONObject responseObject = new JSONObject();
        responseObject.put("count", this.storeService.updateCountMinus(user, index));
        responseObject.put("price", this.storeService.getCartItemPrice(user, index));
        return responseObject.toString();
    }

    // 주문버튼 눌렀을 때 orders 테이블에 상품 정보 insert
    // cart -> order 로 상품정보 넘겨주기
    @PostMapping(value = "order",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postOrder(@SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "order") String orderStr) throws JsonProcessingException {
        // orderStr : "[{"itemIndex":"7","count":"3","orderColor":"핑크","orderSize":"250"},{"itemIndex":"7","count":"4","orderColor":"몰라","orderSize":"오윤정"},{"itemIndex":"6","count":"8","orderColor":"그레이","orderSize":"230"}]"
        // 위에꺼 왜 쓰냐면 반복문으로 폼 데이터 보내면 폼 데이터 여러개를 보내버리는데 그거 넘 안 좋은 방법이라서
        // 아예 자스에서 배열로 담은 걸 하나의 문자열로 만들어가지고 컨트롤러로 보내줘서 그걸 다시 배열로 풀어낸다.

        OrderEntity[] orders = new ObjectMapper().readValue(orderStr, OrderEntity[].class);
        Enum<?> result;
        result = this.storeService.addOrders(user, orders);
//        Enum<?> result;
//
        JSONObject responseObject = new JSONObject();
//
//        if (user == null) {
//            result = CommonResult.FAILURE;
//        } else {
//
//            order.setUserEmail(user.getEmail());
//
//            result = this.storeService.addOrder(order);
//
////            if (result == CommonResult.SUCCESS) {
////                responseObject.put("index", order.getIndex());
////            }
//        }
//
        responseObject.put("result", result.name().toLowerCase());
        responseObject.put("orderNum", orders[0].getOrderNum());
//
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

        LocalDateTime date = LocalDateTime.now();
        date = date.plusDays(2);

        String nowDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        modelAndView.addObject("user", user);
        // 입금기한 : 2일으로 줌
        modelAndView.addObject("date", nowDate);


        return modelAndView;
    }

    // order 요소 불러오기
    @GetMapping(value = "orderItem",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrderVo[] getOrderItem(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "num") BigInteger orderNum) {

        JSONArray responseArray = new JSONArray();

        OrderVo[] orders = this.storeService.getOrders(user, orderNum);
        return orders;
    }


    // 결제완료 누르면 회원정보 및 결제정보 업데이트 및 카트 삭제
    @PatchMapping(value = "orderSuccess",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchOrderSuccess(@SessionAttribute(value = "user", required = false) UserEntity user,
                                    OrderEntity order) {

        Enum<?> result = this.storeService.executeOrder(user, order);
        JSONObject responseObject = new JSONObject();

        responseObject.put("result", result.name().toLowerCase());

//        if (result == CommonResult.SUCCESS) {
//            responseObject.put("index", index);
//        }

        return responseObject.toString();
    }


    @RequestMapping(value = "orderSuccess",
            method = RequestMethod.GET)
    public ModelAndView getOrderSuccess(@SessionAttribute(value = "user", required = false) UserEntity user,
                                        @RequestParam(value = "num") BigInteger orderNum) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("store/orderSuccess");
            OrderVo[] orders = this.storeService.getOrder(user, orderNum);

            Date date = orders[0].getOrderDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            String date2 = formatter.format(date);

            modelAndView.addObject("order", orders[0]);
            modelAndView.addObject("count", orders.length);
            modelAndView.addObject("date", date2);
        }

        return modelAndView;
    }


}
