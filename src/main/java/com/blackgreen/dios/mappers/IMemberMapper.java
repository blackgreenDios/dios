package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.member.EmailAuthEntity;
import com.blackgreen.dios.entities.member.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    //password 값 받을꺼면 원래 String password 넣어야하는데 MemberMapper에서 SET에 기본키인 email 빼고 싹 다 넣어줬으니까 괄호 안에도 싹 다 넣어줘야함 전체를 의미하는게 UserEntity user 래
    //글고 update랑 insert는 무조건 int, 왜냐? MemberMapper에서 LIMIT 1걸어주는 것도 그렇고
    //MemberService에서  if (this.memberMapper.updateUser(ex) == 0) 이거 할때 어짜피 안되서 값 못들어가면 0이고 들어가면 1임 그니까 거의 무조건 int

    UserEntity selectUserByEmailPassword(@Param(value = "email") String email,
                                         @Param(value="password") String password);

    UserEntity selectUserByNameContact(@Param(value = "name") String name,
                                       @Param(value="contact") String contact);




}



// Param 어노테이션을 사용시 Xml에서 parameterType을 사용하지 않고도 쓸 수 있다.
// xml에서 memomapper에서 매개변수를 받을경우 xml에서 parameter를 사용해야한다.