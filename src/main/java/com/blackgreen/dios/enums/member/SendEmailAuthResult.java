package com.blackgreen.dios.enums.member;

import com.blackgreen.dios.interfaces.IResult;

//멤버 서비스 안에 SendEmail 의 결과
//이메일 인증번호 할 때 성공 실패 말고 다른 결과
//이미 사용중인 이메일 주소 입니다.
public enum SendEmailAuthResult implements IResult {
    EMAIL_DUPLICATED
}
