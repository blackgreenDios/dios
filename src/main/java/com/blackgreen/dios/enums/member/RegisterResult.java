package com.blackgreen.dios.enums.member;

import com.blackgreen.dios.interfaces.IResult;

public enum RegisterResult implements IResult {
    EMAIL_NOT_VERIFIED,
    Contact // 번호 중복
}
