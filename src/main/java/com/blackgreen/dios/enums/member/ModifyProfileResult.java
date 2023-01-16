package com.blackgreen.dios.enums.member;

import com.blackgreen.dios.interfaces.IResult;

public enum ModifyProfileResult implements IResult {
    NOT_ALLOWED, //게시글 작성자랑 로그인한 사람이랑 같지않음
    NOT_SIGNED //로그인하지 않음
}
