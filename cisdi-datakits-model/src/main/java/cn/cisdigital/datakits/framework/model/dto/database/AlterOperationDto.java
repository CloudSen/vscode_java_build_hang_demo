package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.AlterEnum;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-11-08-18:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlterOperationDto implements Serializable {
    /**
     * 修改类型
     */
    AlterEnum alterEnum;
    /**
     * 变更后的列信息（增加列 修改 需要实例化此对象）
     */
    List<AlterColumnDto> columnList;
}
