package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.*;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.goods.AddReviewResult;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.GoodsService;
import com.blackgreen.dios.services.RollbackException;
import com.blackgreen.dios.vos.GoodsVo;
import com.blackgreen.dios.vos.ReviewVo;
import jdk.jfr.Category;
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
import java.util.Collection;

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

        ItemColorEntity[] colors = this.goodsService.getColor();
        SellerEntity[] sellers = this.goodsService.getSeller();
        SizeEntity[] sizes = this.goodsService.getSize();
        ItemCategoryEntity[] categories = this.goodsService.getItemCategory();

        modelAndView.addObject("color", colors);
        modelAndView.addObject("size", sizes);
        modelAndView.addObject("seller", sellers);
        modelAndView.addObject("category", categories);

        return modelAndView;
    }



    @PostMapping(value = "write")
    @ResponseBody


    public String postWrite(ItemEntity item,
                            @RequestParam(value = "sizes", required = false) String[] sizeId,
                            @RequestParam(value = "colors", required = false) String[] colorIds,
                            @RequestParam(value = "images", required = false) MultipartFile images) throws IOException {

        Enum<?> result = this.goodsService.addItem(item, images);
        System.out.println(item.getIndex());
        System.out.println("---");

        ItemColorEntity[] itemColors = new ItemColorEntity[colorIds.length];
        for (int i = 0; i < itemColors.length; i++) {
            ItemColorEntity itemColor = new ItemColorEntity();
            itemColor.setId(colorIds[i]);
            itemColors[i] = itemColor;
        }

        for (ItemColorEntity itemColor : itemColors) {
            System.out.println(itemColor.getId());
            itemColor.setItemIndex(item.getIndex());
        }

        System.out.println("---");
        SizeEntity[] sizes = new SizeEntity[sizeId.length];
        for (int i = 0; i < sizes.length; i++) {
            SizeEntity size = new SizeEntity();
            size.setId(sizeId[i]);
            sizes[i] = size;
        }
        for (SizeEntity size : sizes) {
            System.out.println(size.getId());
        }

        JSONObject responseObject = new JSONObject();
        System.out.println("check image" + images);


        for (SizeEntity size : sizes) {
            System.out.println("size check" + size.getId());
            size.setItemIndex(item.getIndex());//인덱스에 저장한 후 size 에 insert
        }

//        for (ItemColorEntity color:colors) {
//            System.out.println("size check" + color.getId());
//            color.setItemIndex(item.getIndex());//인덱스에 저장한 후 color 에 insert
//            System.out.println(color.getItemIndex());
//        }

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

        if (result == CommonResult.SUCCESS) {   // html 에 넘겨줄려고
            ItemColorEntity[] colors = this.goodsService.getColor();
            SellerEntity[] sellers = this.goodsService.getSeller();
            SizeEntity[] sizes = this.goodsService.getSize();
            ItemCategoryEntity[] categories = this.goodsService.getItemCategory();

            modelAndView.addObject("gid", item.getIndex());
            modelAndView.addObject("color", colors);
            modelAndView.addObject("size", sizes);
            modelAndView.addObject("seller", sellers);
            modelAndView.addObject("category", categories);
        }

        return modelAndView;
    }

    @RequestMapping(value = "modify",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody //xhr 로 반환 받을 것들은 무조건 ResponseBody 붙여준다.
    public String patchModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "gid") int gid,
                              ItemEntity item) {
        item.setIndex(gid);
        Enum<?> result = this.goodsService.ModifyItem(item, user);
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

//        SizeEntity[] sizes = new SizeEntity[this.goodsService.getSize().length];
//        int count = 0;
//        for (int i = 0; i <sizes.length ; i++) {
//            SizeEntity size = this.goodsService.getItemSize(gid);
//            count =+ 1;
//            sizes[i] = size;
//        }
//
//        modelAndView.addObject("sizes", sizes);


//        ItemColorEntity[] colors = new ItemColorEntity[goods.getColors().length];

//        for (int i = 0; i < colors.length; i++) {
//            colors[i] = this.goodsService.getItemColor(goods.getColors()[i]);
//        }
//
//        for (ItemColorEntity color : colors) {
//            System.out.println(color.getId());
//            System.out.println(color.getItemIndex());
//            System.out.println(color.getText());
//        }

        modelAndView.addObject("goods", goods);
        modelAndView.addObject("category", this.goodsService.getCategory(goods.getCategoryId()));
        modelAndView.addObject("seller", this.goodsService.getBrand(goods.getSellerIndex()));


//        modelAndView.addObject("color", this.goodsService.getItemColor(goods.getColors()));
//        modelAndView.addObject("Size", goods.getSizes());

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
    public String deleteReview( @SessionAttribute(value = "user", required = false) UserEntity user,
                                ReviewEntity review) {
        Enum<?> result = this.goodsService.deleteReview(user,review);
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name().toLowerCase());
        return responseJson.toString();
    }

    @PatchMapping(value = "review",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody

    public String patchRecoverComment(@SessionAttribute(value = "user", required = false) UserEntity user, ReviewEntity review) {
        Enum<?> result = this.goodsService.recoverReview(review, user);
        System.out.println(result);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    @PostMapping(value = "review")
    @ResponseBody
    public String postReview(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "images", required = false) MultipartFile[] images, ReviewEntity review) throws IOException, RollbackException {
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

    @GetMapping(value = "list")
    public ModelAndView getList(@RequestParam(value = "cad") String cad, //카테고리 아이디
                                @RequestParam(value = "page", required = false,
                                        defaultValue = "1") Integer page,
                                @RequestParam(value = "criterion", required = false) String criterion,
                                @RequestParam(value = "keyword", required = false) String keyword) {
        page = Math.max(1, page);
        ModelAndView modelAndView = new ModelAndView("store/list");

        ItemCategoryEntity[] categories = this.goodsService.getItemCategory();

        modelAndView.addObject("categories",categories);

        ItemCategoryEntity category = this.goodsService.getCategory(cad);
        modelAndView.addObject("category", category.getText()); // id값으로 카테고리 가져오기

        int totalCount; //pagination 하려고 가져온 것
        if ( category != null) {
            totalCount = this.goodsService.getItemCount(category, criterion, keyword);
            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            GoodsVo[] goods = this.goodsService.getItems(category, paging,criterion,keyword);
            modelAndView.addObject("goods", goods);
        }

        return modelAndView;
    }

}
