package cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DorisStreamLoadConfigDto {
    private String host;
    private int port;
    private String user;
    private String passwd;
    private String databaseName;
    private String tableName;
}
