package com.blackgreen.dios.enums.goods;

import com.blackgreen.dios.interfaces.IResult;

public enum AddReviewResult implements IResult {
    SUCCESS,
    FAILURE,
    NOT_SIGNED,
    NOT_ALLOWED
}
