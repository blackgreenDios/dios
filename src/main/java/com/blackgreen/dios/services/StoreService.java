package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.bbs.CommentEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.record.ElementEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.CommentDeleteResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IStoreMapper;
import com.blackgreen.dios.vos.store.CartVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service(value = "com.blackgreen.dios.services.StoreService")
public class StoreService {

    private final IStoreMapper storeMapper;

    public StoreService(IStoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    // cart에 담긴 상품 불러오기
    public CartVo[] getCarts (UserEntity user) {

        return this.storeMapper.selectCartByEmail(user.getEmail());
    }

    // cart 선택상품 삭제
    public Enum<? extends IResult> deleteCart (UserEntity user, CartEntity cart){
        cart = this.storeMapper.selectCartByIndex(cart.getIndex());

        if(cart == null){
            return CommonResult.FAILURE;
        }

        return this.storeMapper.deleteCartByIndex(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 상품수량 변경 : 더하기, 빼기
    public Enum<? extends IResult> updateCountPlus (int index) {
        CartEntity cart = this.storeMapper.selectCartByIndex(index);

        return this.storeMapper.updateCountPlus(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Enum<? extends IResult> updateCountMinus (int index) {
        CartEntity cart = this.storeMapper.selectCartByIndex(index);

        return this.storeMapper.updateCountMinus(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }



}
