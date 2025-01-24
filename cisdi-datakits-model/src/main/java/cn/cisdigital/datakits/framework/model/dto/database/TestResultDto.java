package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 测试结果dto
 *
 * @author xxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestResultDto {

    /**
     * 测试是否通过
     */
    boolean result;

    /**
     * 连接报错信息
     */
    String errorMsg;
}

