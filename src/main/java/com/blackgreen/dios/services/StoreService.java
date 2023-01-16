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
    public CartVo[] getCarts(UserEntity user) {

        return this.storeMapper.selectCartByEmail(user.getEmail());
    }

    // cart 선택상품 삭제
    public Enum<? extends IResult> deleteCart(UserEntity user, CartEntity[] carts) {

        int count = 0;
        for (CartEntity cart : carts) {
            count += this.storeMapper.deleteCartByIndex(user.getEmail(), cart.getIndex());
        }

        return count == carts.length
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 상품수량 변경 : 더하기, 빼기
    public int updateCountPlus(UserEntity user, int index) {
        CartVo cart = this.storeMapper.selectCartByIndex(user.getEmail(), index);

        int count = cart.getCount();
        cart.setCount(count + 1);
        this.storeMapper.updateCount(cart);

        return cart.getCount();
    }

    public int updateCountMinus(UserEntity user, int index) {
        CartVo cart = this.storeMapper.selectCartByIndex(user.getEmail(), index);

        int count = cart.getCount();
        cart.setCount(count - 1);
        this.storeMapper.updateCount(cart);

        return cart.getCount();
    }

    public int getCartItemPrice (UserEntity user, int index) {

        CartVo cart =  this.storeMapper.selectCartByIndex(user.getEmail(), index);

        return cart.getPrice();
    }


    // cart -> order 로 상품정보 넘겨주기
    public Enum<? extends IResult> addOrders(UserEntity user, OrderEntity[] orders) {
        BigInteger orderNum = new BigInteger(RandomStringUtils.randomNumeric(5) + Math.abs(user.getEmail().hashCode()));

        int count = 0;

        for (OrderEntity order : orders) {
            ItemEntity item = this.goodsMapper.selectItemByIndex(order.getItemIndex());
            order.setOrderStatus(0);
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
    public OrderVo[] getOrders(UserEntity user, BigInteger orderNum) {

        return this.storeMapper.selectOrderByEmail(user.getEmail(), orderNum);
    }


    // 결제 완료 누르면 회원정보 및 결제정보 업데이트 및 카트 삭제
    public Enum<? extends IResult> executeOrder(UserEntity user, OrderEntity orderInfo) {
        if (user == null) {
            return CommonResult.FAILURE;
        }
        int count = 0;
        OrderEntity[] orders = this.storeMapper.selectOrderByOrderNum(orderInfo.getOrderNum());
        for (OrderEntity order : orders) {
            // 삭제
            count += this.storeMapper.deleteCartByIndex(user.getEmail(), order.getCartIndex());

            // 정보수정
            order.setUserName(orderInfo.getUserName());
            order.setUserContact(orderInfo.getUserContact());
            order.setUserAddressPostal(orderInfo.getUserAddressPostal());
            order.setUserAddressPrimary(orderInfo.getUserAddressPrimary());
            order.setUserAddressSecondary(orderInfo.getUserAddressSecondary());
            order.setMessage(orderInfo.getMessage());
            order.setPaymentMethod(orderInfo.getPaymentMethod());
            order.setOrderStatus(1);

            count += this.storeMapper.updateOrder(order);
        }
        return count == orders.length * 2  // 카트와 정보수정이 동시에 일어나야돼서 곱하기 2를 해준다.
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 주문완료 페이지
    public OrderVo[] getOrder (UserEntity user, BigInteger orderNum) {

        return this.storeMapper.selectOrderByEmail(user.getEmail(), orderNum);
    }
}
