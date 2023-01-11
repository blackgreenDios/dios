package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.bbs.ArticleEntity;
import com.blackgreen.dios.entities.bbs.BoardEntity;
import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.entities.store.SellerEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.member.ModifyProfileResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.BbsService;
import com.blackgreen.dios.services.MemberService;
import com.blackgreen.dios.utils.CryptoUtils;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
//import com.blackgreen.dios.vos.store.OrderVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.apache.catalina.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;


@Controller(value = "com.blackgreen.dios.controllers.MemberController")
@RequestMapping(value = "/dios")
public class MemberController {
    private final MemberService memberService;
    private final BbsService bbsService;

    @Autowired
    public MemberController(MemberService memberService, BbsService bbsService) {
        this.memberService = memberService;
        this.bbsService = bbsService;
    }
//    -----------------이까지는 의존성 주입을 위한 것.---------------------------------

    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user, EmailAuthEntity auth, UserEntity newUser) throws NoSuchAlgorithmException {
        ModelAndView modelAndView = new ModelAndView("member/register");
        Enum<? extends IResult> result = this.memberService.register(user, auth, newUser);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView("member/register");
        return modelAndView;
    }

    @RequestMapping(value = "email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postEmail(UserEntity user, EmailAuthEntity emailAuth) throws NoSuchAlgorithmException, MessagingException {
        Enum<? extends IResult> result = this.memberService.sendEmailAuth(user, emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.verifyEmailAuth(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //로그인 페이지
    @RequestMapping(value = "login",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView Login() {
        ModelAndView modelAndView = new ModelAndView("member/login");
        return modelAndView;
    }

    @RequestMapping(value = "login",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(HttpSession session, UserEntity user) {

        Enum<?> result = this.memberService.login(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
            //세선 저장소로 부터 값을 불러온다.
            System.out.println("성공");
        } else {
            System.out.println("실패");
        }

        return responseObject.toString();
    }

    //이메일 찾기
    @RequestMapping(value = "findEmail",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverEmail() {
        ModelAndView modelAndView = new ModelAndView("member/findEmail");
        return modelAndView;
    }

    @RequestMapping(value = "findEmail",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverEmail(UserEntity user) {
        Enum<?> result = this.memberService.findEmail(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("email", user.getEmail());
        }
        return responseObject.toString();
    }

    //비밀번호 재설정
    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    //ModelAndView 는 ResponseBody 없어도됨
    public ModelAndView getRecoverPassword() {
        ModelAndView modelAndView = new ModelAndView("member/recoverPassword");
        return modelAndView;
    }

    @RequestMapping(value = "recoverPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPassword(EmailAuthEntity emailAuth) throws MessagingException {

        Enum<?> result = this.memberService.recoverPasswordSend(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("index", emailAuth.getIndex());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPasswordEmail(EmailAuthEntity emailAuth) {
        //System.out.println(emailAuth.getIndex());
        Enum<?> result = this.memberService.recoverPasswordCheck(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("code", emailAuth.getCode());
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ModelAndView getRecoverPasswordEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPasswordAuth(emailAuth);
        ModelAndView modelAndView = new ModelAndView("member/recoverPasswordEmail");
        modelAndView.addObject("result", result.name());
        return modelAndView;
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.PATCH, //PATCH는 수정
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(EmailAuthEntity emailAuth, UserEntity user, HttpSession session) {
        session.setAttribute("user", null);
        Enum<?> result = this.memberService.recoverPassword(emailAuth, user);
        JSONObject responseObject = new JSONObject();
        //String 을 받아야하면 JSON
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //로그아웃
    @RequestMapping(value = "logout",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogout(HttpSession session) {
        session.setAttribute("user", null);
        //user 값이 null 이 아니면 로그인이 된거 - user가 쿠키값인거 같음
        //user 값이 null 이면 로그아웃
        ModelAndView modelAndView = new ModelAndView("redirect:login");
        //로그인 페이지로 리다이랙션(다시 돌아간다)
        //주소에 login 대신 logout 넣으면 다시 login 페이지로 넘어가고 상단바에 로그아웃이랑 마이페이지 없어짐
        return modelAndView;
    }


    //마이페이지
    @RequestMapping(value = "myPage",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView myPage(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:login");
        } else {
            modelAndView = new ModelAndView("member/myPage");
        }

        return modelAndView;
    }

    //마이페이지 수정
    @RequestMapping(value = "myPage",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMyPage(@SessionAttribute(value = "user", required = false) UserEntity signedUser,
                              @RequestParam(value = "newImage", required = false) MultipartFile newImage,
                              UserEntity newUser) throws IOException {
//        System.out.println(newImage.getOriginalFilename());

        Enum<?> result;
        if (signedUser == null) {
            result = ModifyProfileResult.NOT_SIGNED;
        } else {
            result = this.memberService.updateMyPage(signedUser, newUser, newImage);
        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            responseObject.put("nickname", signedUser.getNickname());
            responseObject.put("image", signedUser.getImage());
            responseObject.put("imageType", signedUser.getImageType());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "myPageModify",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView myPageModify(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:login");
        } else {
            modelAndView = new ModelAndView("member/myPageModify");
        }
        return modelAndView;
    }

    @RequestMapping(value = "myPageModify",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postMyPageModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                                   @RequestParam(value = "password") String password) {

        Enum<?> result = user != null && user.getPassword().equals(CryptoUtils.hasSha512(password))
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            //session.setAttribute("user", user);
            //session 에 비밀번호 밖에 없는데 이걸 또 적으면 비밀번호밖에 없는 거를 덮어씌움
            //그래서 비밀번호 말고 정보가 안뜸
            //세선 저장소로 부터 값을 불러온다.
            System.out.println("성공");

            responseObject.put("nickname", user.getNickname());
            responseObject.put("name", user.getName());
            responseObject.put("contact", user.getContact());
            responseObject.put("addressPrimary", user.getAddressPrimary());
            responseObject.put("addressPostal", user.getAddressPostal());
            responseObject.put("addressSecondary", user.getAddressSecondary());
        } else {
            System.out.println("실패");
        }

        return responseObject.toString();
    }


    @RequestMapping(value = "myPageModify",
            method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMyPageModify(@SessionAttribute(value = "user", required = false) UserEntity signedUser,
                                    UserEntity newUser) {
        //매개변수가 많은거는 좋지않음
        //signedUser : 로그인한 유저
        //newUser : DB에 있는 유저 = 회원정보 수정 들어가면 있는 유저 정보들
        Enum<?> result = this.memberService.updateMyPageModify(signedUser, newUser);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //카카오 로그인
    @GetMapping(value = "kakao",
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ModelAndView getKaKao(@RequestParam(value = "code") String code,
                                 @RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "error_description", required = false) String errorDescription,
                                 HttpSession session) throws IOException {

        String accessToken = this.memberService.getKakaoAccessToken(code);
        UserEntity user = this.memberService.getKakaoUserInfo(accessToken); //서비스 호출

        session.setAttribute("user", user);
        return new ModelAndView("member/kakao");

    }

    //마이페이지 비밀번호 재설정
    @RequestMapping(value = "myPagePassword", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPagePassword(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:login");
        } else {
            modelAndView = new ModelAndView("member/myPagePassword");
        }

        return modelAndView;
    }

    @GetMapping(value = "myPageList", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageList(@RequestParam(value = "bid", required = false) String bid,
                                      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "criterion", required = false) String criterion,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @SessionAttribute(value = "user", required = false) UserEntity user) {
        page = Math.max(1, page);

        ModelAndView modelAndView = new ModelAndView("member/myPageList");
        BoardEntity board = this.bbsService.getBoard("free");

        ArticleEntity article = new ArticleEntity();


        modelAndView.addObject("board", board);
        if (board != null && user != null) {
            article.setUserEmail(user.getEmail());
            int totalCount = this.bbsService.getArticleCountByEmailFromFree(article, criterion, keyword);

            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            ArticleReadVo[] articles = this.bbsService.getArticlesByUserEmailFree(article, paging, criterion, keyword);
            modelAndView.addObject("articles", articles);

        } else {
            article.setUserEmail("");
        }
        return modelAndView;
    }

    @GetMapping(value = "myPageQna", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageQna(@RequestParam(value = "bid", required = false) String bid,
                                     @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                     @RequestParam(value = "criterion", required = false) String criterion,
                                     @RequestParam(value = "keyword", required = false) String keyword, @SessionAttribute(value = "user", required = false) UserEntity user) {
        page = Math.max(1, page);

        ModelAndView modelAndView = new ModelAndView("member/myPageQna");
        BoardEntity board = this.bbsService.getBoard("qna");

        ArticleEntity article = new ArticleEntity();


        modelAndView.addObject("board", board);
        if (board != null && user != null) {
            article.setUserEmail(user.getEmail());
            int totalCount = this.bbsService.getArticleCountByEmailFromQna(article, criterion, keyword);

            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            ArticleReadVo[] articles = this.bbsService.getArticlesByUserEmailQna(article, paging, criterion, keyword);
            modelAndView.addObject("articles", articles);
        } else {
            article.setUserEmail("");
        }
        return modelAndView;
    }

    //이미지 다운로드
    @RequestMapping(value = "profileImage", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProfileImage(@SessionAttribute(value = "user") UserEntity user) {
        UserEntity image = this.memberService.getProfileImage(user);

        if (image.getImage() == null) {
            byte[] altBody = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAQQAAAEECAIAAABBat1dAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo1NDZCQjc2Q0E3QjYxMUU1OTdENkI4REEzNDVDREY5MCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo1NDZCQjc2REE3QjYxMUU1OTdENkI4REEzNDVDREY5MCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjU0NkJCNzZBQTdCNjExRTU5N0Q2QjhEQTM0NUNERjkwIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjU0NkJCNzZCQTdCNjExRTU5N0Q2QjhEQTM0NUNERjkwIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+0lMXqAAAO4JJREFUeNrsfWeT3EaS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");
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.valueOf("image/png"));

            return new ResponseEntity<>(altBody, headers, HttpStatus.OK);
        }
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.valueOf(image.getImageType()));
        headers.setContentLength(image.getImage().length);

        headers.add("Content-Type", image.getImageType());
        return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "myPage",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteProfileImage(@SessionAttribute(value = "user", required = false) UserEntity signedUser,
                                     @RequestParam(value = "newImage", required = false) MultipartFile newImage,
                                     UserEntity newUser) throws IOException {
        Enum<?> result;
        if (signedUser == null) {
            result = ModifyProfileResult.NOT_SIGNED;
        } else {
            result = this.memberService.deleteProfileImage(signedUser, newUser, newImage);
        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            responseObject.put("image", signedUser.getImage());
            responseObject.put("imageType", signedUser.getImageType());
        }
        return responseObject.toString();
    }

    //회원탈퇴
    @RequestMapping(value = "myPageDelete", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageDelete() {
        ModelAndView modelAndView = new ModelAndView("member/myPageDelete");
        return modelAndView;
    }

    @DeleteMapping(value = "myPageDelete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteMyPage(@SessionAttribute(value = "user", required = false) UserEntity user, HttpSession session) {
        Enum<?> result = this.memberService.deleteUser(user);
        session.setAttribute("user", null);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("user", user.getEmail());
        }
        //
        return responseObject.toString();

    }

    //주문내역 조회
    @RequestMapping(value = "myPageDeliveryCheck",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView myPageDeliveryCheck(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @SessionAttribute(value = "user", required = false) UserEntity user) {
        page = Math.max(1, page);
        ModelAndView modelAndView;
        OrderVo orderList = new OrderVo();
        if (user == null) {
            modelAndView = new ModelAndView("redirect:login");
        } else {
            modelAndView = new ModelAndView("member/myPageDeliveryCheck");
            orderList.setUserEmail(user.getEmail());
            int totalCount = this.memberService.getOrderList(orderList);
            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            OrderVo[] orders = this.memberService.getOrderListByEmail(orderList, paging);
            DecimalFormat df = new DecimalFormat("###,###");

            OrderVo[] deliveries = this.memberService.orderList(user);

            int beforeDelivery = 0;
            int doingDelivery = 0;
            int completeDelivery = 0;


            for (OrderVo order : orders) {
                int priceCnt = 0;
                priceCnt = order.getPrice() * order.getCount();
                order.setPriceCnt(df.format(priceCnt));
            }
            for(OrderVo delivery : deliveries ){
                if(delivery.getOrderStatus() == 1){
                    beforeDelivery ++;
                } else if (delivery.getOrderStatus() == 2) {
                    doingDelivery ++;
                } else if (delivery.getOrderStatus() == 3) {
                    completeDelivery ++ ;
                }

            }
            modelAndView.addObject("orders", orders);
            modelAndView.addObject("before",beforeDelivery);
            modelAndView.addObject("doing",doingDelivery);
            modelAndView.addObject("complete", completeDelivery);
        }
        return modelAndView;
    }

//    @GetMapping(value = "orderList", produces = MediaType.TEXT_HTML_VALUE)
//    @ResponseBody
//    public String getMyPageDeliveryCheck(@SessionAttribute(value = "user", required = false) UserEntity user) {
//
//        JSONArray responseArray = new JSONArray();
//
//        OrderVo[] orderList = this.memberService.orderList(user);
//
//
//        for (OrderVo order : orderList) {
//            JSONObject orderListObject = new JSONObject();
//
//            orderListObject.put("index", order.getIndex());
//            orderListObject.put("userEmail", order.getUserEmail());
//            orderListObject.put("orderNum", order.getOrderNum());
//            orderListObject.put("count", order.getCount());
//            orderListObject.put("itemIndex", order.getItemIndex());
//            orderListObject.put("orderColor", order.getOrderColor());
//            orderListObject.put("orderSize", order.getOrderSize());
//            orderListObject.put("price", order.getPrice());
//            orderListObject.put("orderStatus", order.getOrderStatus());
//            orderListObject.put("status", order.getStatus());
//            orderListObject.put("storeName", order.getStoreName());
//            orderListObject.put("orderDate", new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderDate()));
//            orderListObject.put("itemName", order.getItemName());
//
//
//            responseArray.put(orderListObject);
//        }
//
//        return responseArray.toString();
//    }

    //주문내역 상세 페이지
    @RequestMapping(value = "myPageDeliveryCheckDetail",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageDeliveryCheckDetail(@SessionAttribute(value = "user", required = false) UserEntity user, OrderVo orderNum) {
        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:login");
        } else {
            modelAndView = new ModelAndView("member/myPageDeliveryCheckDetail");

            OrderVo[] orders = this.memberService.getOrderListByOrderNum(orderNum);
            modelAndView.addObject("orders", orders);

            int totalPrice = 0;
            Date orderDate = null;

            DecimalFormat df = new DecimalFormat("###,###");

            for (OrderVo order : orders) {
                int priceCnt = 0;
                totalPrice += order.getPrice() * order.getCount();
                orderDate = order.getOrderDate();
                priceCnt = order.getPrice() * order.getCount();
                order.setPriceCnt(df.format(priceCnt));
            }

            modelAndView.addObject("totalPrice", df.format(totalPrice));
            modelAndView.addObject("orderDate", orderDate);
        }
        return modelAndView;
    }

}



