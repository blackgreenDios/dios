package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.member.AddImageResult;
import com.blackgreen.dios.enums.member.RegisterResult;
import com.blackgreen.dios.enums.member.SendEmailAuthResult;
import com.blackgreen.dios.enums.member.VerifyEmailAuthResult;
import com.blackgreen.dios.exceptions.RollbackException;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IMemberMapper;
import com.blackgreen.dios.utils.CryptoUtils;
import org.apache.catalina.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service(value = "com.blackgreen.dios.services.memberService")
public class MemberService {
    private final IMemberMapper memberMapper;
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Autowired
    public MemberService(JavaMailSender mailSender, IMemberMapper memberMapper, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.memberMapper = memberMapper;
        this.templateEngine = templateEngine;
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
        return CommonResult.SUCCESS;

    }

    //회원가입
    public Enum<? extends IResult> register(UserEntity user, EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());

        if (existingEmailAuth == null || !existingEmailAuth.IsExpired()) {
            return RegisterResult.EMAIL_NOT_VERIFIED;
        }
        user.setPassword(CryptoUtils.hasSha512(user.getPassword()));
        if (this.memberMapper.insertUser(user) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    @Transactional
    // 인증절차중 중간에 실패를 할경우 다시 처음으로 돌아가게 하는 어노테이션
    public Enum<? extends IResult> sendEmailAuth(UserEntity user, EmailAuthEntity emailAuth) throws NoSuchAlgorithmException, MessagingException {

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
        // 문자열 타입의 authSalt는 String.format의 (ja513698@naver . com0880950 . 9045670 . 868117)이 값으로 들어가게 된다.
//        System.out.println(authSalt);

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
        //Enum<? 는 Enum은 열거 ?는 열거할 때 타입 상관없다는 뜻
        //Enum<?>의 문제점은 SendEmailAuthResult 랑 CommonResult 둘 말고도 다른 열거형도 다 되서 IResult로 두개만 묶어준거임, 이렇게 묶어주면 두개 말고는 안되니까
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                //selectEmailAuthByEmailCodeSalt 안에 email, code,salt 있음
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        //IMemberMapper에 @Param을 쓰면 자동으로 MemberMapper랑 위에 get으로 값 가져왔을 때랑 값이 같다고 인식해줌
        if (existingEmailAuth == null) {
            return CommonResult.FAILURE;
        }
        if (existingEmailAuth.getExpiresOn().compareTo(new Date()) < 0) {
            // compareTo를 호출하는 expiredOn에서 전달하는 Date를 뺀다고 생각하면 됨. (compareTo는 빼기 역할을 한다.) 과거 - 미래 = 음수가 나오기때문에 0보다 작다고 표시함.
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

    //비밀번호 재설정
    @Transactional
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth) throws MessagingException {
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
        //위에서 받고 넘겨주기 때문에 이 밑에 구문에서 받는거 순서 바뀌면 이메일 값 말고 다른 값이 안들어와서 실패했다고 뜸
        Context context = new Context();
        //Service 에서 html 파일에 view 를 넘겨줘야 하기 때문에 (서비스에서 html 파일을 활용하기 위해서)
        context.setVariable("email", emailAuth.getEmail());
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("salt", emailAuth.getSalt());

        String text = this.templateEngine.process("member/recoverPasswordEmailAuth", context);
        // 앞에는 template(setViewName과 같은) 뒤에는 위에서 생성한 code를 변수로 지정한것.
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("guswl0490111@gmail.com");
        helper.setTo(emailAuth.getEmail());
        helper.setSubject("[DIOS] 비밀번호 재설정 안내");
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
            //select 해온 것들이 null 이거나 isExpired() 호출결과가 false인 경우 FAILURE
            return CommonResult.FAILURE;
        }
        UserEntity existingUser = this.memberMapper.selectUserByEmail(existingEmailAuth.getEmail());
        //existingEmailAuth를 받은 existingUser는 이메일, 코드, 솔트까지 다 가지고 있음
        //여기서 테이블에 있는 모든 값들 고대로 다 꺼내서 밑에 setPassword 구문에서 비밀번호만 업데이트 해주는 거임
        //user.getEmail() 하면 이메일이랑 비밀번호만가지고 있고 그러므로 이메일이랑 비밀번호만 들어가서 업데이트 자체가 안됨 ->  나머지는 NOT NULL 이라서
//-----------------------------------------------------------------
        //Select한 객체가 가진 email값으로 새로운 UserEntity 타입의 객체를 Select 해온다.
        //SELECT 한 UserEntity 타입의 객체의 password 필드 값을 전달 받은 UserEntity 타입의 객체가 가진 password 값에 대한 SHA-512 해시값으로 수정한다.
        //user는 이메일이랑 비밀번호만 가지고 있음 즉, user는 그냥 껍데기
        //이 구문에서 새롭게 입력된 비밀번호를 DB에 넣고 업데이트까지
        existingUser.setPassword(CryptoUtils.hasSha512(user.getPassword()));

        //MemberMapper.xml 랑 IMemberMapper 에 updateUser 추가, WHERE 안에는 email 써야함(Primary Key), set 안에는 이메일 제외한 나머지

        if (this.memberMapper.updateUser(existingUser) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // 프로필 이미지 업데이트
    @Transactional
    public Enum<? extends IResult> updateProfile(UserEntity user, MultipartFile[] images) throws RollbackException {
        if (user == null) {
            return AddImageResult.NOT_SIGNED;
        }
        if (this.memberMapper.updateUser(user) == 0) {
            return AddImageResult.FAILURE;
        }

        //여기도 먼가 존나 이상 images 왜 안쓰는거 같노
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                UserEntity profileImage = this.memberMapper.selectUserByEmail(user.getEmail());
                profileImage.setImage(user.getImage());
                profileImage.setImageType(user.getImageType());
                if (this.memberMapper.updateUser(profileImage) == 0) {
                    throw new RollbackException();
                    //예외 터지면 아무 익셉션으로 감
                }
            }
        }
        return AddImageResult.SUCCESS;
    }
}

