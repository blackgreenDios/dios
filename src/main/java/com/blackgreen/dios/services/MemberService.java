package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.bbs.ArticleEntity;
import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.member.*;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IBbsMapper;
import com.blackgreen.dios.mappers.IMemberMapper;
import com.blackgreen.dios.models.PagingModel;
import com.blackgreen.dios.utils.CryptoUtils;
//import com.blackgreen.dios.vos.store.OrderVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.apache.catalina.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service(value = "com.blackgreen.dios.services.memberService")
public class MemberService {
    private final IMemberMapper memberMapper;
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;
    private final IBbsMapper bbsMapper;

    @Autowired
    public MemberService(JavaMailSender mailSender, IMemberMapper memberMapper, TemplateEngine templateEngine, IBbsMapper bbsMapper) {
        this.mailSender = mailSender;
        this.memberMapper = memberMapper;
        this.templateEngine = templateEngine;
        this.bbsMapper = bbsMapper;
    }


    @Transactional
    public Enum<? extends IResult> login(UserEntity user) {

        UserEntity existingUser = this.memberMapper.selectUserByEmailPassword(
                user.getEmail(),
                CryptoUtils.hasSha512(user.getPassword())
        );
        // 들어 온 값이 원래 있던 값이랑 같지 않으면 아예 안들어감
        if (existingUser == null) {
            return CommonResult.FAILURE;
            //그래서 null이면 실패인거
        }

        user.setPassword(existingUser.getPassword());
        user.setNickname(existingUser.getNickname());
        user.setName(existingUser.getName());
        user.setContact(existingUser.getContact());
        user.setAddressPostal(existingUser.getAddressPostal());
        user.setAddressPrimary(existingUser.getAddressPrimary());
        user.setAddressSecondary(existingUser.getAddressSecondary());
        user.setRegisteredOn(existingUser.getRegisteredOn());
        user.setAdmin(existingUser.isAdmin());

        return CommonResult.SUCCESS;

    }

    //회원가입
    public Enum<? extends IResult> register(UserEntity user, EmailAuthEntity emailAuth, UserEntity newUser) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());

        if (existingEmailAuth == null || !existingEmailAuth.IsExpired()) {
            return RegisterResult.EMAIL_NOT_VERIFIED;
        }


        UserEntity userByNickname = this.memberMapper.selectUserByNickname(newUser.getNickname());
        if (userByNickname != null && !user.getEmail().equals(userByNickname.getEmail())) {
            //userByNickname 비어있지않으면 DB에 같은 닉네임이 있다는거
            //근데 로그인한 이메일이랑 중복된 닉네임을 가지고 있는 이메일이랑 같으면 ㄱㅊ 내꺼니까
            //근데 다르면 중복
            return DuplicationResult.NICKNAME;
        }

        //연락처 중복검사
        UserEntity userByContact = this.memberMapper.selectUserByContact(newUser.getContact());
        if (userByContact != null && !user.getEmail().equals(userByContact.getEmail())) {
            return DuplicationResult.CONTACT;
        }


        user.setPassword(CryptoUtils.hasSha512(user.getPassword()));
        if (this.memberMapper.insertUser(user) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    @Transactional
    // 인증 절차중 중간에 실패를 할경우 다시 처음으로 돌아가게 하는 어노테이션
    public Enum<? extends IResult> sendEmailAuth(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest request) throws NoSuchAlgorithmException, MessagingException {

        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());

        if (existingUser != null) {
            // null이 아니라는거는 이미 사용중인 이메일이라는 뜻
            return SendEmailAuthResult.EMAIL_DUPLICATED; //널이 아니면 중복이라는 뜻
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f%f",
                user.getEmail(),
                authCode,
                Math.random(),
                Math.random());
        StringBuilder authSaltHashBuilder = new StringBuilder();
        // StringBuilder 타입의 authSaltHashBuilder 변수를 새객체로 생성.
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        //암호화해줄라고 하는거
        // SHA-512는 생성된 고유값이 512비트이다. 128자리 문자열을 반환한다.
        //아래에서 바이트로 처리를 해준다. 왜냐하면 기존에는 비트이기 때문이다.
        md.update(authSalt.getBytes(StandardCharsets.UTF_8));
        //StandardCharsets 은 UTF-8 쓸려면 넣어야함
        // md의 update는 바이트 단위만 받기 때문에 위에서 authSalt를 변경시켜준다.
        // 이때 반드시 UTF-8로 된 getBytes메서드를 사용한다.
        for (byte hashByte : md.digest()) {
            // .digest()는 바이트배열로 해쉬를 반환
            authSaltHashBuilder.append(String.format("%02x", hashByte)); //format : 16진수(%x)로 형식 설정
            // System.out.println(authSaltHashBuilder);
        }
        authSalt = authSaltHashBuilder.toString();
        //System.out.println(authSalt);

        Date createdOn = new Date();
        // 만료시간이 5분이라서 밑에 DateUtils를 사용하여 5분의 시간을 더해준값으로 명시해준다.
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);
        // createdOn 이 실행된시간 expiresOn이 만료시간
        emailAuth.setCode(authCode);// 랜덤한 6자리 변수
        emailAuth.setSalt(authSalt);// String.format으로 해싱(암호화)된 salt -> salt는 암호화할라고 쓰는거 없으면 password 암호화 안됨
        emailAuth.setEmail(user.getEmail());//user의 이메일
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);// 위에서 새로 지정한 5분이 추가된 변수
        emailAuth.setIsExpired(false); // 기본값을 false로 명시.
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE; // 어떠한 이유에 의해서 실패(중복은 아님 위에서 걸러지기 때문에)
        }
        Context context = new Context();
        //Service에서 html파일에 view를 넘겨줘야 하기 때문에 (서비스에서 html파일을 활용하기 위해서)
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("domain", String.format("%s://%s:%d",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort()));

        String text = this.templateEngine.process("member/registerEmailAuth", context);
        // 앞에는 template(setViewName과 같은) 뒤에는 위에서 생성한 code를 변수로 지정한것.
        MimeMessage mail = this.mailSender.createMimeMessage(); //MimeMessage = 메일 보내려고 쓰는거
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        //MimeMessageHelper = 메일내용에 이미지나 첨부파일 같은거 같이 보낼려고 쓰는거
        helper.setFrom("guswl0490111@gmail.com"); //보내는 사람
        helper.setTo(user.getEmail()); //받는 사람
        helper.setSubject("[DIOS] 환영합니다. 회원가입 완료를 위해 확인해주세요.");
        helper.setText(text, true);
        // true를 반드시 작성해야함(html을 사용할것인지에 대한 여부)
        this.mailSender.send(mail);
        return CommonResult.SUCCESS;
    }

    //회원가입 페이지에서 이메일 인증 버튼 눌렀을 때 이메일 인증 버튼 누르고 인증번호 확인까지
    @Transactional
    public Enum<? extends IResult> verifyEmailAuth(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null) {
            return CommonResult.FAILURE;
        }
        if (existingEmailAuth.getExpiresOn().compareTo(new Date()) < 0) {
            return VerifyEmailAuthResult.EXPIRED;
        }

        existingEmailAuth.setIsExpired(true);

        if (this.memberMapper.updateEmailAuth(existingEmailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    //이메일 찾기
    @Transactional
    public Enum<? extends IResult> findEmail(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByNameContact(
                user.getName(),
                user.getContact()
        );
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }
        user.setEmail(existingUser.getEmail());
        return CommonResult.SUCCESS;
    }

    //비밀번호 재설정 - 전달받은 UserEntity 객체가 가지는 email값을 이메일로 가지는 회원이 테이블에 있으면 success를, 없다면 fail을 JSON 형태로 반환하는 로직을 작성
    @Transactional
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth, HttpServletRequest request) throws MessagingException {
        if (this.memberMapper.selectUserByEmail(emailAuth.getEmail()) == null) {
            //null이라면, 이 이메일을 가지고 있는 회원이 없다는 뜻
            return CommonResult.FAILURE;
        }

        //이메일은 사이트에서 쳐서 들어가 있고, 코드랑 솔트랑 바로 밑에 구문에서 보냄
        String authCode = RandomStringUtils.randomNumeric(6); //랜덤 6자리 숫자
        String authSalt = String.format("%s%s%f%f",
                authCode,
                emailAuth.getEmail(),
                Math.random(),
                Math.random());
        authSalt = CryptoUtils.hasSha512(authSalt);
        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5); //5분 미래
        emailAuth.setCode(authCode);
        emailAuth.setSalt(authSalt);
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);
        emailAuth.setIsExpired(false);
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        Context context = new Context();
        context.setVariable("email", emailAuth.getEmail());
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("salt", emailAuth.getSalt());
        context.setVariable("domain", String.format("%s://%s:%d",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort()));

        String text = this.templateEngine.process("member/recoverPasswordEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("guswl0490111@gmail.com");
        helper.setTo(emailAuth.getEmail());
        helper.setSubject("[DIOS]비밀번호 재설정 인증 번호");
        helper.setText(text, true); // true를 반드시 작성해야함(html을 사용할것인지에 대한 여부)
        this.mailSender.send(mail);
        return CommonResult.SUCCESS; //null 값이면 실패
    }

    public Enum<? extends IResult> recoverPasswordCheck(EmailAuthEntity emailAuth) {//emailAuth 얘가 가지고 있는 값은 인덱스뿐
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByIndex(emailAuth.getIndex());
        if (existingEmailAuth == null || !existingEmailAuth.IsExpired()) {
            return CommonResult.FAILURE;
        }
        emailAuth.setCode(existingEmailAuth.getCode());
        emailAuth.setSalt(existingEmailAuth.getSalt());
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> recoverPasswordAuth(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null) {//select 해온 것들이 null 인가
            return CommonResult.FAILURE;
        }
        if (existingEmailAuth.getExpiresOn().compareTo(new Date()) < 0) {
            return CommonResult.FAILURE;
        }
        existingEmailAuth.setIsExpired(true);
        if (this.memberMapper.updateEmailAuth(existingEmailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // 비밀번호 재설정
    @Transactional
    public Enum<? extends IResult> recoverPassword(EmailAuthEntity emailAuth, UserEntity user) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null || existingEmailAuth.getExpiresOn().compareTo(new Date()) < 0) {
            return CommonResult.FAILURE;
        }
        UserEntity existingUser = this.memberMapper.selectUserByEmail(existingEmailAuth.getEmail());
        existingUser.setPassword(CryptoUtils.hasSha512(user.getPassword()));
        if (this.memberMapper.updateUser(existingUser) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }


    //닉네임 수정
    @Transactional
    public Enum<? extends IResult> updateMyPage(UserEntity signedUser, UserEntity newUser, MultipartFile image) throws IOException {

        if (signedUser == null) {
            return ModifyProfileResult.NOT_SIGNED;
        }

        //닉네임 중복검사
        UserEntity userByNickname = this.memberMapper.selectUserByNickname(newUser.getNickname());
        if (userByNickname != null && !userByNickname.getEmail().equals(signedUser.getEmail())) {
            return DuplicationResult.NICKNAME;
        }

        signedUser.setNickname(newUser.getNickname());
        signedUser.setImage(image == null ? null : image.getBytes()); //조건 안걸면 이미지가 null 일 때 오류뜸
        signedUser.setImageType(image == null ? null : image.getContentType()); //마찬가지

        if (this.memberMapper.updateUserByMayPage(signedUser) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }


    // 회원정보 수정
    @Transactional
    public Enum<? extends IResult> updateMyPageModify(UserEntity signedUser, UserEntity newUser) {
        //여기도 매개변수 많은거 좋지 않음
        //signedUser : 로그인한 유저
        //newUser : 회원정보 수정 들어가면 있는 유저 정보들

        //닉네임 중복검사
        UserEntity userByNickname = this.memberMapper.selectUserByNickname(newUser.getNickname());
        if (userByNickname != null && !signedUser.getEmail().equals(userByNickname.getEmail())) {
            //userByNickname 비어있지않으면 DB에 같은 닉네임이 있다는거
            //근데 로그인한 이메일이랑 중복된 닉네임을 가지고 있는 이메일이랑 같으면 ㄱㅊ 내꺼니까
            //근데 다르면 중복
            return DuplicationResult.NICKNAME;
        }

        //연락처 중복검사
        UserEntity userByContact = this.memberMapper.selectUserByContact(newUser.getContact());
        if (userByContact != null && !signedUser.getEmail().equals(userByContact.getEmail())) {
            return DuplicationResult.CONTACT;
        }

        //중복이 아니라면 업데이트 해줌
        signedUser.setNickname(newUser.getNickname()); //웹에서 바뀐 유저 정보(newUser) 가져와서 로그인한 유저한테 새로 넣고
        signedUser.setName(newUser.getName());
        signedUser.setContact(newUser.getContact());
        signedUser.setAddressPostal(newUser.getAddressPostal());
        signedUser.setAddressPrimary(newUser.getAddressPrimary());
        signedUser.setAddressSecondary(newUser.getAddressSecondary());
        return this.memberMapper.updateUser(signedUser) > 0 //바뀐 유저 정보를 DB로 업데이트
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    //카카오 로그인
    public String getKakaoAccessToken(String code, String redirectUri) throws IOException {
        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=b53a656bcd965d745a55ca52a6ccd639");
                requestBuilder.append(String.format("&redirect_uri=%s", redirectUri));
                requestBuilder.append("&code=").append(code);
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
//            System.out.println("응답코드:" + responseCode);
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
//            System.out.println("응답내용:" + responseBuilder);

        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        return responseObject.getString("access_token");
    }

    public UserEntity getKakaoUserInfo(String accessToken) throws IOException {

        URL url = new URL("https://kapi.kakao.com/v2/user/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
//        System.out.println("응답 코드 : " + responseCode);

        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
//        System.out.println("응답내용:" + responseBuilder);
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject propertyObject = responseObject.getJSONObject("properties");

        String email = String.valueOf(responseObject.getLong("id"));

        UserEntity user = this.memberMapper.selectUserByEmail(email);

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setNickname(propertyObject.getString("nickname"));
            user.setPassword(""); //카카오 로그인은 비밀번호 못땡겨옴
            user.setName("");
            user.setContact(email);
            user.setAddressPrimary("");
            user.setAddressPostal("");
            user.setAddressSecondary(""); //빈 문자열로 하면 웹에서 입력안해도 Insert 가능

            this.memberMapper.insertUser(user);
        }


        return user;

    }

    //프로필 이미지 수정
    public UserEntity getProfileImage(UserEntity user) {
        return this.memberMapper.selectImageByEmail(user.getEmail());
    }

    @Transactional
    public Enum<? extends IResult> deleteProfileImage(UserEntity signedUser, UserEntity newUser, MultipartFile image) throws IOException {

        if (signedUser == null) {
            return ModifyProfileResult.NOT_SIGNED;
        }


        if (this.memberMapper.deleteUserByMayPage(signedUser) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    //회원탈퇴
    public Enum<? extends IResult> deleteUser(UserEntity user) {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
        if (existingUser == null) {
            return CommonResult.FAILURE;
        }

        user.setEmail(existingUser.getEmail());

        return this.memberMapper.deleteUser(user.getEmail()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    @Transactional
    public OrderVo[] orderList(UserEntity user) {
        System.out.println(user.getEmail());
        return this.memberMapper.selectOrderList(user.getEmail());
    }


    public int getOrderList(OrderVo orderList) {
        return this.memberMapper.selectOrderListCount(orderList.getUserEmail(), orderList.getOrderStatus());
    }

    public OrderVo[] getOrderListByEmail(OrderVo orderList, PagingModel paging) {
        return this.memberMapper.selectOrderListByUserEmail(
                orderList.getUserEmail(),
                orderList.getOrderStatus(),
                paging.countPerPage,
                (paging.requestPage - 1) * paging.countPerPage);

    }

    public OrderVo[] getOrderListByOrderNum(OrderVo orderList) {
        return this.memberMapper.selectOrderListDetail(
                orderList.getOrderNum());
    }



}

