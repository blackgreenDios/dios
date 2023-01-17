package com.blackgreen.dios.enums;

import com.blackgreen.dios.interfaces.IResult;

//공통적인 결과값들을 열거, 공통된 값들을 땡겨쓸려고
//성공, 실패
public enum CommonResult implements IResult {
    FAILURE, //인증번호 및 솔트(Salt) 생성 후 테이블에 인서트
    SUCCESS, //인증번호를 전송하였습니다.
    NOT,
    NOT_USER
}
