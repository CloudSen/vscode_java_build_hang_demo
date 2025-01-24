package cn.cisdigital.datakits.framework.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * 数据源参数Dto
 * @author xxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParamDto implements Serializable {

    /**
     * 参数键
     */
    String paramKey;
    /**
     * 参数值
     */
    String paramValue;
}
