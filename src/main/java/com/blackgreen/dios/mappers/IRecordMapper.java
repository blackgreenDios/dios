package com.blackgreen.dios.mappers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.record.CountEntity;
import com.blackgreen.dios.entities.record.ElementEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.interfaces.IResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Mapper
public interface IRecordMapper {

    // 목표개수 설정하기
    UserEntity selectUserByEmail(@Param(value = "email") String email);
    int updateUser(UserEntity user);

    // count insert 하기
    int insertCount(CountEntity count);

    // 기록장 작성
    int insertRecord(ElementEntity element);

    // 기록장 작성한 거 불러오기 : 사진, 메모
    ElementEntity selectRecordByEmailDate (@Param(value = "userEmail") String userEmail,
                                             @Param(value = "todayDate") Date todayDate);



    // 기록장 작성한 거 불러오기 : count
    CountEntity selectCountByEmail (@Param(value = "userEmail") String userEmail,
                                    @Param(value = "todayDate") Date todayDate);


    // 왼쪽 화살표 눌렀을 때 (이전 날짜로 가기)
    Date selectPreviousDate(@Param(value = "userEmail") String userEmail,
                            @Param(value = "todayDate") Date todayDate);

    // 오른쪽 화살표 눌렀을 때 (다음 날짜로 가기)
    Date selectNextDate(@Param(value = "userEmail") String userEmail,
                        @Param(value = "todayDate") Date todayDate);


}
