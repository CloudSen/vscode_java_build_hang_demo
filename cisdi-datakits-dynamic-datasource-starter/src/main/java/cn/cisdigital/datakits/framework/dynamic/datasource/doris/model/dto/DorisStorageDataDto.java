package cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DorisStorageDataDto implements Serializable {

    /**
     * 目的库或schema名
     */
    String targetDatabaseName;
    /**
     * 目的表名
     */
    String targetTableName;
    /**
     * 目的库的列和值
     */
    List<DorisColumnValueDto> targetColumnValueList;
}
