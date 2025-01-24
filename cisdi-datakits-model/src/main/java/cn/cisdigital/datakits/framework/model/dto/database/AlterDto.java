package cn.cisdigital.datakits.framework.model.dto.database;

import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-11-04-15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlterDto extends TableBase implements Serializable {

    /**
     * 修改类型 todo本次迭代仅仅支持 add drop
     */
    List<AlterOperationDto> alterList;

    public AlterDto(TableBase tableBase){
      this.setSchema(tableBase.getSchema());
      this.setDataBaseTypeEnum(tableBase.getDataBaseTypeEnum());
      this.setTableName(tableBase.getTableName());
    }
}
