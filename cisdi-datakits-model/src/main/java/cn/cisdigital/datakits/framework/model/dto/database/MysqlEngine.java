package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * MySQL引擎信息
 *
 * @author xxx
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MysqlEngine {

    String engine;
    String support;
    String comment;
    String transactions;
    String xa;
    String savepoints;
}
