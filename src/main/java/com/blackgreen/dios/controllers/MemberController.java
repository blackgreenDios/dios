package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.ImageEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.member.AddImageResult;
import com.blackgreen.dios.enums.member.ModifyProfileResult;
import com.blackgreen.dios.exceptions.RollbackException;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.services.MemberService;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ModelAndView MyPage() {
        ModelAndView modelAndView = new ModelAndView("member/myPage");
        return modelAndView;
    }

    @RequestMapping(value = "image",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "id") int id) {
        // 주소를 들어가면 이미지 자체를 보여주기 위해 ResponseEntity
        ImageEntity image = this.memberService.getImage(id);

        //id에 -1이나 9999를 집어넣으면 null이 나올 수도 있음
        //image 가 null 일 때 404
        //있을 떄 200
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 이미지라는 걸 알려주기 위함 - 그래서 Mime 설정해준거
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", image.getFileMime());
        //주소에서 받아지는 걸 이미지라는 걸 결정하는게 Mime = header

        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
        //()안에는 생성자 메서드, 생성자 호출, 생성자 매개변수
        //실제 이미지 데이터, 이게 이미지인지 판단하는거, 상태

    }

    //이미지 업로드
    @RequestMapping(value = "image",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postImage(@RequestParam(value = "upload") MultipartFile file) throws IOException {
        // 브라우저에 이름이 upload라고 정해져있음 따라야지 뭐
        // System.out.println(image.getOriginalFilename());
        // 콘솔에 파일의 이름이 찍힘
        ImageEntity image = new ImageEntity();
        image.setFileName(file.getOriginalFilename());
        image.setFileMime(file.getContentType());
        image.setData(file.getBytes());

        Enum<?> result = this.memberService.addImage(image);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            //SUCCESS 일 때 이미지를 다운받을 수 있는 주소를 넣음
            responseObject.put("url", "http://localhost:8080/dios/image?id=" + image.getIndex());
        }
        return responseObject.toString();
    }


    @RequestMapping(value = "myPage",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchMyPage(@SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "nickname") String nickname) {
        Enum<?> result;
        if (user == null) {
            result = ModifyProfileResult.NOT_SIGNED;
        } else {
            result = this.memberService.updateMyPage(user,nickname);
        }
        System.out.println("새로운 닉네임 : " + nickname);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            responseObject.put("nickname", user.getNickname());
        }
        return responseObject.toString();

    }
}

//
//        if (result == CommonResult.SUCCESS) {
//            responseObject.put("images",image );
//        }



