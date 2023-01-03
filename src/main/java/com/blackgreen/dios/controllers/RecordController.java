package com.blackgreen.dios.controllers;

import com.blackgreen.dios.entities.member.UserEntity;
import com.blackgreen.dios.entities.record.CountEntity;
import com.blackgreen.dios.entities.record.ElementEntity;
import com.blackgreen.dios.enums.CommonResult;
import com.blackgreen.dios.enums.bbs.WriteResult;
import com.blackgreen.dios.services.RecordService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller(value = "com.blackgreen.dios.controllers.RecordController")
@RequestMapping(value = "/record")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // 목표설정 화면
    @RequestMapping(value = "setting",
            method = RequestMethod.GET)
    public ModelAndView getSetting(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("records/setting");
        }

        return modelAndView;
    }

    //
    // 목표설정 (목표개수 입력)함
    @RequestMapping(value = "setting",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchSetting(@SessionAttribute(value = "user", required = false) UserEntity user,
                               @RequestParam(value = "goalCount", required = false) int count) {
        Enum<?> result = this.recordService.updateGoalCount(user, count);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    // 스쿼트
    @RequestMapping(value = "squat",
            method = RequestMethod.GET)
    public ModelAndView getSquat(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("records/squat");

            int goal = this.recordService.readCount(user);
            modelAndView.addObject("goal", goal);
        }
        return modelAndView;
    }

    // 런지
    @RequestMapping(value = "lunge",
            method = RequestMethod.GET)
    public ModelAndView getLunge(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("records/lunge");
        }

        return modelAndView;
    }

    // 플랭
    @RequestMapping(value = "plank",
            method = RequestMethod.GET)
    public ModelAndView getPlank(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("records/plank");
        }

        return modelAndView;
    }

    //
    // count insert 하기 (목표 개수 성공했을 때 record 누르면 실행되는 거)
    @RequestMapping(value = "squat",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postSquat(@SessionAttribute(value = "user", required = false) UserEntity user, CountEntity count) throws ParseException {

        count.setUserEmail(user.getEmail());

        // 현재 날짜
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // 문자열 -> Date
        String nowDate = formatter.format(date);
        Date now = formatter.parse(nowDate);

        count.setTodayDate(now);


        Enum<?> result;
        JSONObject responseObject = new JSONObject();

        result = this.recordService.insertCount(count);

        responseObject.put("date", nowDate);
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    // 기록장
    // TODO: 로그인 안하면 기록장 자체도 못 들어가게 해야함
    @RequestMapping(value = "recordBook",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecordBook(@SessionAttribute(value = "user", required = false) UserEntity user,
                                      @RequestParam(value = "dt", required = false) String dtStr) throws ParseException {
        ModelAndView modelAndView;

        if (user == null) {
            modelAndView = new ModelAndView("redirect:/dios/login");
        } else {
            modelAndView = new ModelAndView("records/recordBook");
        }

        // 현재 날짜
        Date date;
        if (dtStr == null) {
            date = new Date();
        } else {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dtStr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        final String email = user.getEmail();
        CountEntity count = this.recordService.getCount(email, date);
        ElementEntity element = this.recordService.getRecords(email, date);


        // 만약에 아무것도 작성되지 않았을 땐 불러올 게 없으니까 일단 빈 엔티티 만들어줘야 500이 안 떠
        if (element == null) {
            element = new ElementEntity();
        }

        if (count == null) {
            count = new CountEntity();
        }

        modelAndView.addObject("element", element);
        modelAndView.addObject("count", count);
        // 현재 날짜
        modelAndView.addObject("date", formatter.format(date));

        // 왼쪽 화살표 눌렀을 때 (이전 날짜로 가기)
        Date previousDate = this.recordService.getPreviousDate(email, date);
        String previousDateStr = previousDate == null ? null : formatter.format(previousDate);
        modelAndView.addObject("previousDate", previousDateStr);

        // 오른쪽 화살표 눌렀을 때 (다음 날짜로 가기)
        Date nextDate = this.recordService.getNextDate(email, date);
        String nextDateStr = nextDate == null ? null : formatter.format(nextDate);
        modelAndView.addObject("nextDate", nextDateStr);


        return modelAndView;
    }

    // 기록장 작성
    @PostMapping(value = "recordBook")
    @ResponseBody
    public String postRecordBook(@SessionAttribute(value = "user", required = false) UserEntity user,
                                 @RequestParam(value = "dt", required = false) String dtStr,
                                 ElementEntity element) throws ParseException {

        Enum<?> result;
        JSONObject responseObject = new JSONObject();

        // 날짜
        Date date;
        if (dtStr == null) {
            date = new Date();
        } else {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dtStr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String nowDate = formatter.format(date);


        if (user == null) {
            result = CommonResult.FAILURE;
        } else {

            element.setUserEmail(user.getEmail());
            element.setTodayDate(date);

            result = this.recordService.addRecord(element);
        }

        responseObject.put("result", result.name().toLowerCase());
        responseObject.put("date", nowDate);

        return responseObject.toString();
    }

    // 기록장 : 이미지
    @PostMapping(value = "photo")
    @ResponseBody
    public String postPhoto(@SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "images", required = false) MultipartFile images,
                            @RequestParam(value = "dt", required = false) String dtStr,
                            ElementEntity element) throws IOException, ParseException {

        Enum<?> result;
        JSONObject responseObject = new JSONObject();

        // 날짜
        Date date;
        if (dtStr == null) {
            date = new Date();
        } else {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dtStr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String nowDate = formatter.format(date);


        if (user == null) {
            result = CommonResult.FAILURE;
        } else {

            element.setUserEmail(user.getEmail());
            element.setTodayDate(date);

            result = this.recordService.addRecordPhoto(element, images);
        }

        responseObject.put("result", result.name().toLowerCase());
        responseObject.put("date", nowDate);

        return responseObject.toString();
    }


    // diary 삭제
    @RequestMapping(value = "recordBook",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String DeleteDiary(ElementEntity element,
                              @SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "dt", required = false) String dtStr) throws ParseException {

        // 현재 날짜
        Date date;
        if (dtStr == null) {
            date = new Date();
        } else {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dtStr);
        }

        Enum<?> result = this.recordService.ClearDiary(user.getEmail(), date);

        JSONObject responseObject = new JSONObject();

        responseObject.put("date", dtStr);
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    // add 삭제
    @RequestMapping(value = "recordBook",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String DeleteAdd(ElementEntity element,
                            @SessionAttribute(value = "user", required = false) UserEntity user,
                            @RequestParam(value = "dt", required = false) String dtStr) throws ParseException {

        // 현재 날짜
        Date date;
        if (dtStr == null) {
            date = new Date();
        } else {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dtStr);
        }

        Enum<?> result = this.recordService.ClearAdd(user.getEmail(), date);

        JSONObject responseObject = new JSONObject();

        responseObject.put("date", dtStr);
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    // photo 삭제

}
