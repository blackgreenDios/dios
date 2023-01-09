package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.entities.store.ItemEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.store.CountResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IGoodsMapper;
import com.blackgreen.dios.mappers.IStoreMapper;
import com.blackgreen.dios.vos.store.CartVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

@Service(value = "com.blackgreen.dios.services.StoreService")
public class StoreService {

    private final IStoreMapper storeMapper;
    private final IGoodsMapper goodsMapper;

    public StoreService(IStoreMapper storeMapper, IGoodsMapper goodsMapper) {
        this.storeMapper = storeMapper;
        this.goodsMapper = goodsMapper;
    }

    // cart에 담긴 상품 불러오기
    public CartVo[] getCarts (UserEntity user) {

        return this.storeMapper.selectCartByEmail(user.getEmail());
    }

    // cart 선택상품 삭제
    public Enum<? extends IResult> deleteCart (CartEntity[] carts) {

        int count = 0;
        for (CartEntity cart : carts) {
            count += this.storeMapper.deleteCartByIndex(cart.getIndex());
        }

        return count == carts.length
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
    public Enum<? extends IResult> addOrders (UserEntity user, OrderEntity[] orders) {
        BigInteger orderNum = new BigInteger(RandomStringUtils.randomNumeric(5) + Math.abs(user.getEmail().hashCode()));

        int count = 0;

        for (OrderEntity order : orders) {
            ItemEntity item = this.goodsMapper.selectItemByIndex(order.getItemIndex());
            order.setOrderStatus(1);
            order.setUserEmail(user.getEmail());
            order.setOrderNum(orderNum);
            order.setPrice(item.getPrice());

            // orders 의 배열의 길이만큼 상품이 insert 되면 성공
            count += this.storeMapper.insertOrder(order);
        }
        return count == orders.length
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }


    // order에 담긴 상품 불러오기
    public OrderVo[] getOrders (UserEntity user, String orderNum) {

        return this.storeMapper.selectOrderByEmail(user.getEmail(), orderNum);
    }


}
