package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class TestVo implements Serializable {

    private LocalDateTime localDateTime;
    private LocalTime localTime;
    private LocalDate localDate;
    private Long along;
}
