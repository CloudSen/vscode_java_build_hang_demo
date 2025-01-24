package cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DorisColumnValueDto implements Serializable {
    /**
     * 列名
     */
    String columnName;
    /**
     * 值或函数
     */
    Object value;
    /**
     * 值是否是函数名称，true：只把值写到header里，不写到body里， false：需要把数据写到body里，header里只需要填列名
     */
    boolean functionValue;
}
