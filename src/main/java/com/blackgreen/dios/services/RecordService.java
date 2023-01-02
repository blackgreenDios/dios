package com.blackgreen.dios.services;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.record.CountEntity;
import com.blackgreen.dios.entities.record.ElementEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.CommonUpdateResult;
import com.blackgreen.dios.interfaces.IResult;
import com.blackgreen.dios.mappers.IMemberMapper;
import com.blackgreen.dios.mappers.IRecordMapper;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service(value = "com.blackgreen.dios.services.RecordService")
public class RecordService {

    private final IRecordMapper recordMapper;
    private final IMemberMapper memberMapper;

    public RecordService(IRecordMapper recordMapper, IMemberMapper memberMapper) {
        this.recordMapper = recordMapper;
        this.memberMapper = memberMapper;
    }

    // 목표개수 설정
    // TODO : 이메일부분 세션에서 받아오는 걸로 고쳐야함 ("eun8548@naver.com" 부분)
    public Enum<? extends IResult> updateCount (int count) {
        UserEntity existingUser = this.memberMapper.selectUserByEmail("eun8548@naver.com");

        existingUser.setGoalCount(count);

        return this.recordMapper.updateUser(existingUser) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 스쿼트화면에 목표개수 띄우기
    // TODO : 이메일부분 세션에서 받아오는 걸로 고쳐야함 ("eun8548@naver.com" 부분)
    public int readCount(UserEntity user) {

        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());

        return existingUser.getGoalCount();
    }


    // 기록장 작성 : 이미지
    @Transactional
    public Enum<? extends IResult> addRecord (UserEntity user, ElementEntity element, MultipartFile image) throws IOException {

//        if (user == null) {
//            return CommonResult.FAILURE;
//        }
//
//        element.setUserEmail(user.getEmail());

        element.setUserEmail("eun8548@naver.com");
//        element.setImage(image.getBytes());
//        element.setImageType(image.getContentType());


        if (this.recordMapper.insertRecord(element) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    // count insert 하기 (목표 개수 성공했을 때 record 누르면 실행되는 거 )
    public Enum<? extends IResult> insertCount (CountEntity count) {


        return this.recordMapper.insertCount(count) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 기록장 작성한 거 불러오기 : 사진, 메모
//    public ElementEntity getRecords (ElementEntity element) {
//
//        Date date = new Date();
//
//        ElementEntity existElement = this.recordMapper.selectRecordByEmailDate("eun8548@naver.com", date);
//
//        return existElement;
//    }

    public ElementEntity getRecords (String email, Date date) {

//        if (user == null) {
//            return CommonResult.FAILURE;
//        }

        ElementEntity existingRecord = this.recordMapper.selectRecordByEmailDate(email, date);

//        if (existingRecord.getDiary() == null) {
////            return RecordResult.NULL;
//            return CommonResult.FAILURE;
//        }
//
//        element.setDiary(existingRecord.getDiary());
//
////        return RecordResult.EXIST;
        return existingRecord;
    }

    // 기록장 작성한 거 불러오기 : count
    public CountEntity getCount (String email, Date date) {

//        if (user == null) {
//            return CommonResult.FAILURE;
//        }

        return this.recordMapper.selectCountByEmail(email, date);

    }

    // 왼쪽 화살표 눌렀을 때 (이전 날짜로 가기)
    public Date getPreviousDate(String email, Date date) {
        return this.recordMapper.selectPreviousDate(email, date);
    }

    // 오른쪽 화살표 눌렀을 때 (다음 날짜로 가기)
    public Date getNextDate(String email, Date date) {
        return this.recordMapper.selectNextDate(email, date);
    }


    // diary 삭제인데 하나하나 삭제해야되니까 update
//    public Enum<? extends IResult> updateDiary (String email, Date date) {
//        ElementEntity existingElement = this.recordMapper.selectRecordByEmailDate(email, date);
//
//        return this.recordMapper.updateDiary(existingElement) > 0
//                ? CommonResult.SUCCESS
//                : CommonResult.FAILURE;
//    }

    // diary 삭제
    public Enum<? extends IResult> ClearDiary (String email, Date dt) {
        ElementEntity element = this.recordMapper.selectRecordByEmailDate(email, dt);
        element.setDiary("");
        return this.recordMapper.insertRecord(element) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // add 삭제
    public Enum<? extends IResult> ClearAdd (String email, Date dt) {
        ElementEntity element = this.recordMapper.selectRecordByEmailDate(email, dt);
        element.setAdd("");
        return this.recordMapper.insertRecord(element) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
