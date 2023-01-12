package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.goods.AddReviewResult;
import com.blackgreen.dios.services.GoodsService;
import com.blackgreen.dios.services.RollbackException;
import com.blackgreen.dios.vos.goods.GoodsVo;
import com.blackgreen.dios.vos.goods.ReviewVo;
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

@Controller(value = "com.blackgreen.dios.controllers.GoodsController")
@RequestMapping(value = "/goods")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @RequestMapping(value = "write",
            method = RequestMethod.GET)
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView("goods/write");

        SellerEntity[] sellers = this.goodsService.getSeller();
        ItemCategoryEntity[] categories = this.goodsService.getItemCategory();
        modelAndView.addObject("seller", sellers);
        modelAndView.addObject("category", categories);

        return modelAndView;
    }

    //상품 등록

    @PostMapping(value = "write")
    @ResponseBody


    public String postWrite(ItemEntity item,
                            @SessionAttribute(value = "user",required = false)UserEntity user,
                            @RequestParam(value = "sizes", required = false) String[] sizes,
                            @RequestParam(value = "colors", required = false) String[] colors,
                            @RequestParam(value = "images", required = false) MultipartFile images) throws IOException {

        Enum<?> result = this.goodsService.addItem(user, item, images);

        ItemColorEntity[] itemColors = new ItemColorEntity[colors.length];
        for (int i = 0; i < colors.length; i++) {
            itemColors[i] = new ItemColorEntity();
            itemColors[i].setItemIndex(item.getIndex());
            itemColors[i].setColor(colors[i]);
        }
        this.goodsService.addItemColors(itemColors);

        ItemSizeEntity[] itemSize = new ItemSizeEntity[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            itemSize[i] = new ItemSizeEntity();
            itemSize[i].setItemIndex(item.getIndex());
            itemSize[i].setSize(sizes[i]);
        }
        this.goodsService.addItemSizes(itemSize);

// select 한 값 저장해주는 코드

//        ItemColorEntity[] itemColors = new ItemColorEntity[colorIds.length]; // 새로운 배열 만들기
//        for (int i = 0; i < itemColors.length; i++) {
//            ItemColorEntity itemColor = new ItemColorEntity();
//            ItemColorEntity itemColorById = this.goodsService.getItemColorById(colorIds[i]);
//
//            itemColor.setId(colorIds[i] + i);
//            itemColor.setItemIndex(item.getIndex());
//            itemColor.setText(itemColorById.getText());
//            itemColors[i] = itemColor;
//            this.goodsService.addColor(itemColors[i]);
//        }
//
//
//        System.out.println("---");
//        SizeEntity[] sizes = new SizeEntity[sizeId.length];
//        for (int i = 0; i < sizes.length; i++) {
//            SizeEntity size = new SizeEntity();
//            SizeEntity sizeById = this.goodsService.getItemSizeById(sizeId[i]);
//            size.setId(sizeId[i] + i);
//            size.setItemIndex(item.getIndex());
//            size.setText(sizeById.getText());
//            sizes[i] = size;
//            this.goodsService.addSize(sizes[i]);
//        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("gid", item.getIndex()); //gid는 goodsIndex 의 줄임말이다.
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @GetMapping(value = "image") // 다운로드용 맵핑
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id") int id) {
        ItemImgEntity image = this.goodsService.getImage(id);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", image.getFileMime()); // header의 contentType이 아~주 중요해요 ~!, 이해는 하되,, 외우진 마라
        // 프젝하다가 업로드, 다운로드 할 일이 있는데 이걸 교과서처럼 참고하삼요~!!!
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "image",
            method = RequestMethod.POST, // 업로드를 위한 것
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postImage(@RequestParam(value = "upload") MultipartFile file) throws IOException {

        ItemImgEntity image = new ItemImgEntity();
        image.setFileName(file.getOriginalFilename());
        image.setFileMime(file.getContentType());
        image.setData(file.getBytes());
        Enum<?> result = this.goodsService.addItemImage(image);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("url", "http://localhost:8080/goods/image?id=" + image.getIndex());
        }
        return responseObject.toString();
    }


    //수정 페이지
    //타임리프 쓸때는 responseBody 안붙임 !!!
    @RequestMapping(value = "modify",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)

    public ModelAndView getModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "gid", required = false) int gid) {

        ModelAndView modelAndView = new ModelAndView("goods/modify");
        ItemEntity item = new ItemEntity();
        item.setIndex(gid);

        Enum<?> result = this.goodsService.prepareModifyItem(item, user);
        modelAndView.addObject("item", item);
        modelAndView.addObject("result", result.name());
        SellerEntity[] sellers = this.goodsService.getSeller();
        ItemCategoryEntity[] categories = this.goodsService.getItemCategory();
        modelAndView.addObject("seller", sellers);
        modelAndView.addObject("category", categories);
        ItemColorEntity[] colors = this.goodsService.getItemColors(item.getIndex());
        modelAndView.addObject("colors", colors);
        ItemSizeEntity[] sizes = this.goodsService.getItemSize(item.getIndex());
        modelAndView.addObject("sizes", sizes);

        if (result == CommonResult.SUCCESS) {
            modelAndView.addObject("gid", item.getIndex());
        }

        return modelAndView;
    }

    @RequestMapping(value = "modify",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody //xhr 로 반환 받을 것들은 무조건 ResponseBody 붙여준다.
    public String patchModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "gid") int gid,
                              @RequestParam(value = "images", required = false) MultipartFile images,
                              @RequestParam(value = "sizes", required = false) String[] sizes,
                              @RequestParam(value = "colors", required = false) String[] colors,
                              ItemEntity item) throws IOException {

        item.setIndex(gid);
        Enum<?> result = this.goodsService.ModifyItem(item, user, images);

        if (colors != null) {
            ItemColorEntity[] itemColors = new ItemColorEntity[colors.length];
            for (int i = 0; i < colors.length; i++) {
                itemColors[i] = new ItemColorEntity();
                itemColors[i].setItemIndex(item.getIndex());
                itemColors[i].setColor(colors[i]);
            }
            this.goodsService.addItemColors(itemColors);
        }

        if (sizes!= null){
            ItemSizeEntity[] itemSize = new ItemSizeEntity[sizes.length];
            for (int i = 0; i < sizes.length; i++) {
                itemSize[i] = new ItemSizeEntity();
                itemSize[i].setItemIndex(item.getIndex());
                itemSize[i].setSize(sizes[i]);
            }
            this.goodsService.addItemSizes(itemSize);
        }



        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("gid", gid);
        }
        return responseObject.toString();
    }


    @RequestMapping(value = "read",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getRead(@RequestParam(value = "gid") int gid) {

        ModelAndView modelAndView = new ModelAndView("goods/read");
        GoodsVo goods = this.goodsService.getItem(gid);
        ReviewEntity[] reviews = this.goodsService.getReviews(gid);
        int sum = 0;
        int reviewCount = reviews.length;
        double ScoreAvg = 0;

        for (int i = 0; i < reviews.length; i++) {
            sum += reviews[i].getScore();
        }
        if (reviewCount > 0){
            ScoreAvg = (double )sum / reviewCount;
            ScoreAvg = (double) (Math.round(ScoreAvg*10));
            ScoreAvg = ScoreAvg / 10;

        } else {
            ScoreAvg = 0;
        }
        goods.setScoreAvg(ScoreAvg);

        goods.setIndex(gid);
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("category", this.goodsService.getCategory(goods.getCategoryId()));
        modelAndView.addObject("seller", this.goodsService.getBrand(goods.getSellerIndex()));

        modelAndView.addObject("sizes", this.goodsService.getItemSize(gid));
        modelAndView.addObject("colors", this.goodsService.getItemColors(gid));
        return modelAndView;
    }

    @PostMapping(value = "read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRead(@SessionAttribute(value = "user", required = false) UserEntity user,
                           @RequestParam(value = "itemIndex") int itemIndex,
                           @RequestParam(value = "count") int count,
                           @RequestParam(value = "orderColor") String orderColor,
                           @RequestParam(value = "orderSize") String orderSize,
                           CartEntity cart) throws IOException {

        Enum<?> result;
        cart.setItemIndex(itemIndex);
        cart.setCount(count);
        cart.setOrderSize(orderSize);
        cart.setOrderColor(orderColor);

        JSONObject responseObject = new JSONObject();
        try {
            result = this.goodsService.addCartItem(user, cart);
        } catch (RollbackException ignored) {
            result = AddReviewResult.FAILURE;
        }
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    @RequestMapping(value = "read",
            method = RequestMethod.DELETE,//DELETE로 사용한 이유: 주소를 동일하게 하고 방식을 달리 하는 것은 레스트인데,그냥 삭제할때 이렇게 쓰기로 개발자끼리 약속함
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody //return 값을 그 자체로 넘기기 위해 보냄
    public String deleteRead(@SessionAttribute(value = "user", required = false) UserEntity user,
                             @RequestParam(value = "gid") int gid) {
        ItemEntity item = new ItemEntity();
        item.setIndex(gid);

        Enum<?> result = this.goodsService.deleteItem(item, user);
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("cad", item.getCategoryId());
        }
        return responseJson.toString();
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

    @GetMapping(value = "sellerImage") // 다운로드용 맵핑
    public ResponseEntity<byte[]> getSellerImage(@RequestParam(value = "index") int index) {
        SellerEntity image = this.goodsService.getSellerImage(index);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(image.getImageData().length);
        headers.add("Content-Type", image.getImageMime()); // header의 contentType이 아~주 중요해요 ~!, 이해는 하되,, 외우진 마라
        // 프젝하다가 업로드, 다운로드 할 일이 있는데 이걸 교과서처럼 참고하삼요~!!!
        return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "review", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReviewVo[] getReview(@RequestParam(value = "gid") int goodsIndex) {
        return this.goodsService.getReviews(goodsIndex);
    }

    @DeleteMapping(value = "review", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteReview(@SessionAttribute(value = "user", required = false) UserEntity user,
                               ReviewEntity review) {
        Enum<?> result = this.goodsService.deleteReview(user, review);
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name().toLowerCase());
        return responseJson.toString();
    }

    @PatchMapping(value = "review", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody

    public String patchRecoverReview(@SessionAttribute(value = "user", required = false) UserEntity user, ReviewEntity review) {
        Enum<?> result = this.goodsService.recoverReview(review, user);
        System.out.println(result);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    @PostMapping(value = "review")
    @ResponseBody
    public String postReview(@SessionAttribute(value = "user", required = false) UserEntity user,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             ReviewEntity review) throws IOException, RollbackException {
        JSONObject responseObject = new JSONObject();
        Enum<?> result;
        try {
            result = this.goodsService.addReview(user, review, images);
        } catch (RollbackException ignored) {
            result = AddReviewResult.FAILURE;
        }
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    @GetMapping(value = "reviewImage")
    public ResponseEntity<byte[]> getReviewImage(@RequestParam(value = "index") int index) {
        ReviewImageEntity reviewImage = this.goodsService.getReviewImage(index);
        ResponseEntity<byte[]> responseEntity;
        if (reviewImage == null) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(reviewImage.getType()));
            headers.setContentLength(reviewImage.getData().length);
            responseEntity = new ResponseEntity<>(reviewImage.getData(), HttpStatus.OK);
        }
        return responseEntity;
    }

    @DeleteMapping(value = "colors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteColors(@RequestParam(value = "itemIndex", required = false) int itemIndex,
                               @RequestParam(value = "color", required = false) String[] color) {

        ItemColorEntity[] itemColors = new ItemColorEntity[color.length];
        for (int i = 0; i < color.length; i++) {
            itemColors[i] = new ItemColorEntity();
            itemColors[i].setItemIndex(itemIndex);
            itemColors[i].setColor(color[i]);
        }

        Enum<?> result = this.goodsService.deleteColors(itemColors);
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name().toLowerCase());
        return responseJson.toString();

    }

    @DeleteMapping(value = "sizes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteSizes(@RequestParam(value = "itemIndex", required = false) int itemIndex,
                              @RequestParam(value = "size", required = false) String[] size) {

        ItemSizeEntity[] itemSizes = new ItemSizeEntity[size.length];
        for (int i = 0; i < size.length; i++) {
            itemSizes[i] = new ItemSizeEntity();
            itemSizes[i].setItemIndex(itemIndex);
            itemSizes[i].setSize(size[i]);
        }

        Enum<?> result = this.goodsService.deleteSizes(itemSizes);
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name().toLowerCase());
        return responseJson.toString();

    }


}
