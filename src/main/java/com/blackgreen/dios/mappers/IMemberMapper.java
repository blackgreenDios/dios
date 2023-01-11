package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.ImageEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.store.OrderEntity;
//import com.blackgreen.dios.vos.store.OrderVo;
import com.blackgreen.dios.vos.bbs.ArticleReadVo;
import com.blackgreen.dios.vos.store.OrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;

@Mapper
public interface IMemberMapper {
    int insertEmailAuth(EmailAuthEntity emailAuth);

    // 타입을 int로 한 이유는 레코드의 개수를 가져오기 위함이다.
    int insertUser(UserEntity user);

    UserEntity selectUserByEmail(@Param(value = "email") String email);

    EmailAuthEntity selectEmailAuthByIndex(@Param(value = "index") int index);

    EmailAuthEntity selectEmailAuthByEmailCodeSalt(@Param(value = "email") String email,
                                                   @Param(value = "code") String code,
                                                   @Param(value = "salt") String salt);

    // @Param 어노테이션을 사용하게 되면 Mapper에서 parameterType을 절대 사용해서는 안된다.
    // BINARY는 소문자 대문자를 구별하여 검색하는데 사용.

    int updateEmailAuth(EmailAuthEntity emailAuth);

    int updateUser(UserEntity user);

    int updateUserByMayPage(UserEntity user);

    UserEntity selectUserByEmailPassword(@Param(value = "email") String email,
                                         @Param(value = "password") String password);


    UserEntity selectUserByNameContact(@Param(value = "name") String name,
                                       @Param(value = "contact") String contact);


    //이미지
    int insertImage(ImageEntity image);

    UserEntity selectImageByEmail(@Param(value = "email") String email);


    UserEntity selectUserByNickname(@Param(value = "nickname") String nickname);

    UserEntity selectUserByContact(@Param(value = "contact") String contact);

    int deleteUserByMayPage(UserEntity user);


    int deleteUser(@Param(value = "email") String email);

    //주문내역
    OrderVo[] selectOrderList(@Param(value = "userEmail") String userEmail);

    int selectOrderListCount(@Param(value = "userEmail") String userEmail,@Param(value = "orderStatus") int orderStatus);

    OrderVo[] selectOrderListByUserEmail(
            @Param(value = "userEmail")
            String userEmail,
            @Param(value = "orderStatus") int orderStatus,
            @Param(value = "limit")
            int limit,
            @Param(value = "offset")
            int offset);

    //주문내역 상세페이지
    OrderVo[] selectOrderListDetail(@Param(value = "orderNum") BigInteger orderNum);
}
