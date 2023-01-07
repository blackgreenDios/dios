package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.bbs.CommentEntity;
import com.blackgreen.dios.entities.store.CartEntity;
import com.blackgreen.dios.vos.store.CartVo;
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
    int updateCountPlus (CartEntity cart);
    int updateCountMinus (CartEntity cart);

}
