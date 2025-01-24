package cn.cisdigital.datakits.framework.dynamic.datasource.database.engine;

import cn.hutool.core.collection.CollUtil;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.engine.model.MysqlEngineDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;

import java.util.List;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.DatabaseConstants.DORIS_INNODB_ENGINE_NAME;
import static cn.cisdigital.datakits.framework.dynamic.datasource.common.DatabaseConstants.MYSQL_SHOW_ENGINES;

/**
 * Doris引擎策略
 *
 * @author xxx
 */
public class DorisEngine implements DatabaseEngineStrategy<MysqlEngineDto> {

    @Override
    public DataBaseTypeEnum databaseType() {
        return DataBaseTypeEnum.DORIS;
    }

    @Override
    public String getQueryEnginesSql() {
        return MYSQL_SHOW_ENGINES;
    }

    @Override
    public Class<MysqlEngineDto> getEngineModelClass() {
        return MysqlEngineDto.class;
    }

    @Override
    public boolean doCheckActualDatabaseType(List<MysqlEngineDto> engineModelList) {
        return CollUtil.findOne(engineModelList, engine -> engine.getEngine().equalsIgnoreCase(DORIS_INNODB_ENGINE_NAME)) != null;
    }
}
