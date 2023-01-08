package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.store.CountResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IStoreMapper;
import com.blackgreen.dios.vos.store.CartVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.springframework.stereotype.Service;

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
    public Enum<? extends IResult> deleteCart (int index) {

         CartEntity cart = this.storeMapper.selectCartByIndex(index);

        if (cart == null){
            return CommonResult.FAILURE;
        }

        return this.storeMapper.deleteCartByIndex(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 상품수량 변경 : 더하기, 빼기
    public Enum<? extends IResult> updateCountPlus (int index) {
        CartEntity cart = this.storeMapper.selectCartByIndex(index);

        int count = cart.getCount();
        cart.setCount(count + 1);

        return this.storeMapper.updateCount(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Enum<? extends IResult> updateCountMinus (int index) {
        CartEntity cart = this.storeMapper.selectCartByIndex(index);

        if (cart.getCount() <= 1) {
            return CountResult.OUT_OF_RANGE;
        }
        int count = cart.getCount();
        cart.setCount(count - 1);

        return this.storeMapper.updateCount(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    // cart -> order 로 상품정보 넘겨주기
    public Enum<? extends IResult> addOrder (OrderEntity order) {

        return this.storeMapper.insertOrder(order) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    // order에 담긴 상품 불러오기
    public OrderVo[] getOrders (UserEntity user) {

        return this.storeMapper.selectOrderByEmail(user.getEmail());
    }


}
