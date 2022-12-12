package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;


@Controller(value = "com.blackgreen.dios.controllers.MemberController")
@RequestMapping(value = "/dios")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
//    -----------------이까지는 의존성 주입을 위한 것.---------------------------------

    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user, EmailAuthEntity auth) throws NoSuchAlgorithmException {
        ModelAndView modelAndView = new ModelAndView("member/register");
        Enum<? extends IResult> result = this.memberService.register(user, auth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView("member/register");
        return modelAndView;
    }
    /*
    modelAndView 객체를 설정 해주는 이유는 반환값에 실어서 보내야 하기 때문이다
    ModelAndView에는 3가지 메서드가 존재한다
    setViewName : 뷰의 경로(뷰의 이름)을 설정한다.
    addObject : "변수이름", "데이터값" 이렇게 두가지를 실어서 보낸다.
    addAllObject :
    */

//----------------------------- register 화면을 보여주기 위한 GET 요청 방식 --------------------------------

    @RequestMapping(value = "email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    // produces는 개발자 도구 network에서 ContentType이 정한다.(Header에서)
    @ResponseBody
    // 만약 responseBody가 없으면 return할때 위에 view네임을 적을경우 그 view로 이동하는데 붙여놓고 한다면 그 return 값 자체 즉 어떠한 문자열이 반환된다.
    public String postEmail(UserEntity user, EmailAuthEntity emailAuth) throws NoSuchAlgorithmException, MessagingException {
        // 매개변수를 두개의 Entity를 user, emailAuth를 매개변수로 던진다.
        // emailAuth를 postEmail에 같이 던져주는 이유는 emailAuth
//        System.out.println(user.getEmail());
        Enum<? extends IResult> result = this.memberService.sendEmailAuth(user, emailAuth);
        //Enum타입<? 어떠한 값이든 받는다 . 다만 IResult를 상속하는 범위내에서> result 는 this의 memberService의 메서드 sendEmailAuth의 user변수와 emailAuth의 값을 받는다.
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        // {
        //      "result" : "success"
        //  }가 출력되어진다.
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
        // result의 name은 문자열 타입이기 때문에 이것을 화면에 구현해 주기 위해서는 ResponseBody 어노테이션을 반드시 사용해 주어야 한다.
    }

    @RequestMapping(value = "email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.verifyEmailAuth(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //로그인 페이지 login.html 이랑 연결
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
        //String 을 받아야하면 JSON
        responseObject.put("result", result.name().toLowerCase());

        if (result != CommonResult.SUCCESS) {
            System.out.println("실패");
        }
        session.setAttribute("user", user);
        //세선 저장소로 부터 값을 불러온다.
        System.out.println("성공");
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
        return responseObject.toString();//"{"result":"success"}"라는 String 타입으로 됨
        //ResponseBody 안붙이면 templates 안에 어쩌고 해서 String 으로 안됨
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
    public String patchRecoverPassword(EmailAuthEntity emailAuth, UserEntity user) {
        Enum<?> result = this.memberService.recoverPassword(emailAuth, user);
        JSONObject responseObject = new JSONObject();
        //String 을 받아야하면 JSON
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //마이페이지
    @RequestMapping(value = "myPage",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView MyPage() {
        ModelAndView modelAndView = new ModelAndView("member/myPage");
        return modelAndView;
    }
}
