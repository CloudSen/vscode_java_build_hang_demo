package cn.cisdigital.datakits.framework.dynamic.datasource.database.engine.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * MySQL引擎信息
 *
 * @author xxx
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MysqlEngineDto implements Serializable {

    String engine;
    String support;
    String comment;
    String transactions;
    String xa;
    String savepoints;
}
