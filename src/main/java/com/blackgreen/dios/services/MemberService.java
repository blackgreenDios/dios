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
        // ?????? ??? ?????? ?????? ?????? ????????? ?????? ????????? ?????? ????????????
        if (existingUser == null) {
            return CommonResult.FAILURE;
            //????????? null?????? ????????????
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

    //????????????
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
            //userByNickname ????????????????????? DB??? ?????? ???????????? ????????????
            //?????? ???????????? ??????????????? ????????? ???????????? ????????? ?????? ??????????????? ????????? ?????? ????????????
            //?????? ????????? ??????
            return DuplicationResult.NICKNAME;
        }

        //????????? ????????????
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
    // ?????? ????????? ????????? ????????? ????????? ?????? ???????????? ???????????? ?????? ???????????????
    public Enum<? extends IResult> sendEmailAuth(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest request) throws NoSuchAlgorithmException, MessagingException {

        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());

        if (existingUser != null) {
            // null??? ?????????????????? ?????? ???????????? ?????????????????? ???
            return SendEmailAuthResult.EMAIL_DUPLICATED; //?????? ????????? ??????????????? ???
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f%f",
                user.getEmail(),
                authCode,
                Math.random(),
                Math.random());
        StringBuilder authSaltHashBuilder = new StringBuilder();
        // StringBuilder ????????? authSaltHashBuilder ????????? ???????????? ??????.
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        //????????????????????? ?????????
        // SHA-512??? ????????? ???????????? 512????????????. 128?????? ???????????? ????????????.
        //???????????? ???????????? ????????? ?????????. ???????????? ???????????? ???????????? ????????????.
        md.update(authSalt.getBytes(StandardCharsets.UTF_8));
        //StandardCharsets ??? UTF-8 ????????? ????????????
        // md??? update??? ????????? ????????? ?????? ????????? ????????? authSalt??? ??????????????????.
        // ?????? ????????? UTF-8??? ??? getBytes???????????? ????????????.
        for (byte hashByte : md.digest()) {
            // .digest()??? ?????????????????? ????????? ??????
            authSaltHashBuilder.append(String.format("%02x", hashByte)); //format : 16??????(%x)??? ?????? ??????
            // System.out.println(authSaltHashBuilder);
        }
        authSalt = authSaltHashBuilder.toString();
        //System.out.println(authSalt);

        Date createdOn = new Date();
        // ??????????????? 5???????????? ?????? DateUtils??? ???????????? 5?????? ????????? ?????????????????? ???????????????.
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);
        // createdOn ??? ??????????????? expiresOn??? ????????????
        emailAuth.setCode(authCode);// ????????? 6?????? ??????
        emailAuth.setSalt(authSalt);// String.format?????? ??????(?????????)??? salt -> salt??? ?????????????????? ????????? ????????? password ????????? ??????
        emailAuth.setEmail(user.getEmail());//user??? ?????????
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);// ????????? ?????? ????????? 5?????? ????????? ??????
        emailAuth.setIsExpired(false); // ???????????? false??? ??????.
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE; // ????????? ????????? ????????? ??????(????????? ?????? ????????? ???????????? ?????????)
        }
        Context context = new Context();
        //Service?????? html????????? view??? ???????????? ?????? ????????? (??????????????? html????????? ???????????? ?????????)
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("domain", String.format("%s://%s:%d",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort()));

        String text = this.templateEngine.process("member/registerEmailAuth", context);
        // ????????? template(setViewName??? ??????) ????????? ????????? ????????? code??? ????????? ????????????.
        MimeMessage mail = this.mailSender.createMimeMessage(); //MimeMessage = ?????? ???????????? ?????????
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        //MimeMessageHelper = ??????????????? ???????????? ???????????? ????????? ?????? ???????????? ?????????
        helper.setFrom("guswl0490111@gmail.com"); //????????? ??????
        helper.setTo(user.getEmail()); //?????? ??????
        helper.setSubject("[DIOS] ???????????????. ???????????? ????????? ?????? ??????????????????.");
        helper.setText(text, true);
        // true??? ????????? ???????????????(html??? ????????????????????? ?????? ??????)
        this.mailSender.send(mail);
        return CommonResult.SUCCESS;
    }

    //???????????? ??????????????? ????????? ?????? ?????? ????????? ??? ????????? ?????? ?????? ????????? ???????????? ????????????
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

    //????????? ??????
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

    //???????????? ????????? - ???????????? UserEntity ????????? ????????? email?????? ???????????? ????????? ????????? ???????????? ????????? success???, ????????? fail??? JSON ????????? ???????????? ????????? ??????
    @Transactional
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth, HttpServletRequest request) throws MessagingException {
        if (this.memberMapper.selectUserByEmail(emailAuth.getEmail()) == null) {
            //null?????????, ??? ???????????? ????????? ?????? ????????? ????????? ???
            return CommonResult.FAILURE;
        }

        //???????????? ??????????????? ?????? ????????? ??????, ????????? ????????? ?????? ?????? ???????????? ??????
        String authCode = RandomStringUtils.randomNumeric(6); //?????? 6?????? ??????
        String authSalt = String.format("%s%s%f%f",
                authCode,
                emailAuth.getEmail(),
                Math.random(),
                Math.random());
        authSalt = CryptoUtils.hasSha512(authSalt);
        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5); //5??? ??????
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
        helper.setSubject("[DIOS]???????????? ????????? ?????? ??????");
        helper.setText(text, true); // true??? ????????? ???????????????(html??? ????????????????????? ?????? ??????)
        this.mailSender.send(mail);
        return CommonResult.SUCCESS; //null ????????? ??????
    }

    public Enum<? extends IResult> recoverPasswordCheck(EmailAuthEntity emailAuth) {//emailAuth ?????? ????????? ?????? ?????? ????????????
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
        if (existingEmailAuth == null) {//select ?????? ????????? null ??????
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

    // ???????????? ?????????
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


    //????????? ??????
    @Transactional
    public Enum<? extends IResult> updateMyPage(UserEntity signedUser, UserEntity newUser, MultipartFile image) throws IOException {

        if (signedUser == null) {
            return ModifyProfileResult.NOT_SIGNED;
        }

        //????????? ????????????
        UserEntity userByNickname = this.memberMapper.selectUserByNickname(newUser.getNickname());
        if (userByNickname != null && !userByNickname.getEmail().equals(signedUser.getEmail())) {
            return DuplicationResult.NICKNAME;
        }

        signedUser.setNickname(newUser.getNickname());
        signedUser.setImage(image == null ? null : image.getBytes()); //?????? ????????? ???????????? null ??? ??? ?????????
        signedUser.setImageType(image == null ? null : image.getContentType()); //????????????

        if (this.memberMapper.updateUserByMayPage(signedUser) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }


    // ???????????? ??????
    @Transactional
    public Enum<? extends IResult> updateMyPageModify(UserEntity signedUser, UserEntity newUser) {
        //????????? ???????????? ????????? ?????? ??????
        //signedUser : ???????????? ??????
        //newUser : ???????????? ?????? ???????????? ?????? ?????? ?????????

        //????????? ????????????
        UserEntity userByNickname = this.memberMapper.selectUserByNickname(newUser.getNickname());
        if (userByNickname != null && !signedUser.getEmail().equals(userByNickname.getEmail())) {
            //userByNickname ????????????????????? DB??? ?????? ???????????? ????????????
            //?????? ???????????? ??????????????? ????????? ???????????? ????????? ?????? ??????????????? ????????? ?????? ????????????
            //?????? ????????? ??????
            return DuplicationResult.NICKNAME;
        }

        //????????? ????????????
        UserEntity userByContact = this.memberMapper.selectUserByContact(newUser.getContact());
        if (userByContact != null && !signedUser.getEmail().equals(userByContact.getEmail())) {
            return DuplicationResult.CONTACT;
        }

        //????????? ???????????? ???????????? ??????
        signedUser.setNickname(newUser.getNickname()); //????????? ?????? ?????? ??????(newUser) ???????????? ???????????? ???????????? ?????? ??????
        signedUser.setName(newUser.getName());
        signedUser.setContact(newUser.getContact());
        signedUser.setAddressPostal(newUser.getAddressPostal());
        signedUser.setAddressPrimary(newUser.getAddressPrimary());
        signedUser.setAddressSecondary(newUser.getAddressSecondary());
        return this.memberMapper.updateUser(signedUser) > 0 //?????? ?????? ????????? DB??? ????????????
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    //????????? ?????????
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
//            System.out.println("????????????:" + responseCode);
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
//            System.out.println("????????????:" + responseBuilder);

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
//        System.out.println("?????? ?????? : " + responseCode);

        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
//        System.out.println("????????????:" + responseBuilder);
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject propertyObject = responseObject.getJSONObject("properties");

        String email = String.valueOf(responseObject.getLong("id"));

        UserEntity user = this.memberMapper.selectUserByEmail(email);

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setNickname(propertyObject.getString("nickname"));
            user.setPassword(""); //????????? ???????????? ???????????? ????????????
            user.setName("");
            user.setContact(email);
            user.setAddressPrimary("");
            user.setAddressPostal("");
            user.setAddressSecondary(""); //??? ???????????? ?????? ????????? ??????????????? Insert ??????

            this.memberMapper.insertUser(user);
        }


        return user;

    }

    //????????? ????????? ??????
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

    //????????????
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

