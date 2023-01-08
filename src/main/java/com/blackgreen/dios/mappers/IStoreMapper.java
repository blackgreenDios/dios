package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.bbs.CommentEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
import com.blackgreen.dios.vos.store.CartVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IStoreMapper {

    // cart 에 담긴 상품 불러오기
    CartVo[] selectCartByEmail (@Param(value = "userEmail") String userEmail);

    // cart 선택상품 삭제
    CartEntity selectCartByIndex(@Param(value = "index") int index);
    int deleteCartByIndex (CartEntity cart);

    // 상품수량 변경 : 더하기, 빼기
    int updateCount (CartEntity cart);

    // 주문하기 눌렀을 때 orders 테이블에 상품 정보 insert
    int insertOrder (OrderEntity order);

    // order 에 담긴 상품 불러오기
    OrderVo[] selectOrderByEmail (@Param (value = "userEmail") String userEmail);

}
