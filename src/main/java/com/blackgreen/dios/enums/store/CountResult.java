package com.blackgreen.dios.enums.store;

import com.blackgreen.dios.interfaces.IResult;

public enum CountResult implements IResult {
    OUT_OF_RANGE; // 장바구니 수량이 음수가 되었을 때 경고
}
