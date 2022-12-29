package com.blackgreen.dios.enums.goods;

import com.blackgreen.dios.interfaces.IResult;

public enum GoodsResult implements IResult {
    NOT_ALLOWED, // 권한없음
    NO_SUCH_GOODS // 해당 상품을 찾을수 없음
}
