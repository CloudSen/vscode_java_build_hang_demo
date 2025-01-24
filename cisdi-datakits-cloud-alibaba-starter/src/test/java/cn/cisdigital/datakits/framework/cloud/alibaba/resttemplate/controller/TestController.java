package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.controller;

import cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.vo.TestVo;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@Validated
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/serialize")
    public ResVo<TestVo> testSerialize() {
        TestVo vo = new TestVo();
        vo.setLocalDateTime(LocalDateTime.now());
        vo.setLocalTime(LocalTime.now());
        vo.setLocalDate(LocalDate.now());
        vo.setAlong(1000L);
        return ResVo.ok(vo);
    }
}
