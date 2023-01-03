package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.bbs.ArticleEntity;
import com.blackgreen.dios.entities.bbs.BoardEntity;
import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.member.ModifyProfileResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.services.BbsService;
import com.blackgreen.dios.services.MemberService;
import com.blackgreen.dios.utils.CryptoUtils;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


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
    public String postRegister(UserEntity user, EmailAuthEntity auth,UserEntity newUser) throws NoSuchAlgorithmException {
        ModelAndView modelAndView = new ModelAndView("member/register");
        Enum<? extends IResult> result = this.memberService.register(user, auth,newUser);
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

//
//    @RequestMapping(value = "myPage",
//            method = RequestMethod.PATCH,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public String patchMyPage(@SessionAttribute(value = "user", required = false) UserEntity signedUser,
//                              @RequestParam(value = "image", required = false) MultipartFile image,
//                              UserEntity newUser) throws IOException {
//
////        System.out.println(image.getOriginalFilename());
//
////
////        newUser.setImageMime(image.getContentType());
////        newUser.setImageData(image.getBytes());
//
//        Enum<?> result;
//        if (signedUser == null) {
//            result = ModifyProfileResult.NOT_SIGNED;
//        } else {
//            result = this.memberService.updateMyPage(signedUser,newUser);
//        }
//
//        JSONObject responseObject = new JSONObject();
//        responseObject.put("result", result.name().toLowerCase());
//
//        if (result == CommonResult.SUCCESS) {
//            responseObject.put("nickname", signedUser.getNickname());
////            responseObject.put("image", signedUser.getImageData());
//
//        }
//        return responseObject.toString();
//    }

    @RequestMapping(value = "myPage",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMyPage(@SessionAttribute(value = "user", required = false) UserEntity signedUser,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              UserEntity newUser) throws IOException {

     //   System.out.println(image.getOriginalFilename());
//        newUser.setImageMime(image.getContentType());
//        newUser.setImageData(image.getBytes());

        Enum<?> result;
        if (signedUser == null) {
            result = ModifyProfileResult.NOT_SIGNED;
        } else {
            result = this.memberService.updateMyPage(signedUser,newUser);
        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            responseObject.put("nickname", signedUser.getNickname());
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


    //마이페이지  게시글
//    @RequestMapping(value = "myPageList", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView getMyPageList() {
//        ModelAndView modelAndView = new ModelAndView("member/myPageList");
//        return modelAndView;
//    }

    @GetMapping(value = "myPageList",produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageList(@RequestParam(value = "bid",required = false)String bid,
                                      @RequestParam(value = "page",required = false,defaultValue = "1")Integer page,
                                      @RequestParam(value = "criterion",required = false)String criterion,
                                      @RequestParam(value = "keyword",required = false)String keyword,
                                      @SessionAttribute(value = "user",required = false) UserEntity user){
        page=Math.max(1,page);

        ModelAndView modelAndView=new ModelAndView("member/myPageList");
        BoardEntity board=this.bbsService.getBoardFree(bid);

        ArticleEntity article = new ArticleEntity();


        modelAndView.addObject("board",board);
        if (board!=null){
            article.setUserEmail(user.getEmail());
            int totalCount=this.bbsService.getArticleCountByEmailFromFree(article,criterion,keyword);

            PagingModel paging=new PagingModel(totalCount,page);
            modelAndView.addObject("paging",paging);

            ArticleReadVo[] articles=this.bbsService.getArticles(board,paging,criterion,keyword);
            modelAndView.addObject("articles",articles);

        }
        return modelAndView;
    }

    @GetMapping(value = "myPageQna",produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPageQna(@RequestParam(value = "bid",required = false)String bid,
                                      @RequestParam(value = "page",required = false,defaultValue = "1")Integer page,
                                      @RequestParam(value = "criterion",required = false)String criterion,
                                      @RequestParam(value = "keyword",required = false)String keyword,@SessionAttribute(value = "user",required = false)UserEntity user){
        page=Math.max(1,page);

        ModelAndView modelAndView=new ModelAndView("member/myPageQna");
        BoardEntity board=this.bbsService.getBoardQna(bid);

        ArticleEntity article = new ArticleEntity();


        modelAndView.addObject("board",board);
        if (board!=null){
            article.setUserEmail(user.getEmail());
            int totalCount=this.bbsService.getArticleCountByEmailFromQna(article,criterion,keyword);

            PagingModel paging=new PagingModel(totalCount,page);
            modelAndView.addObject("paging",paging);

            ArticleReadVo[] articles=this.bbsService.getArticles(board,paging,criterion,keyword);
            modelAndView.addObject("articles",articles);

        }
        return modelAndView;
    }

}



