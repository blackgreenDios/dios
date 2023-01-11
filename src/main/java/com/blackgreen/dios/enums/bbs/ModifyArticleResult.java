package com.blackgreen.dios.enums.bbs;

import com.blackgreen.dios.interfaces.IResult;

public enum ModifyArticleResult implements IResult {
    NO_SUCH_ARTICLE, //존재하지 않는 게시글
    NOT_ALLOWED,
    NOT_SIGNED
}
