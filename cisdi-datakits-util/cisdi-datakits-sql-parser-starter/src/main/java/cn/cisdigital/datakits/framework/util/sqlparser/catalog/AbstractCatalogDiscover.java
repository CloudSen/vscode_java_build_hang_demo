package cn.cisdigital.datakits.framework.util.sqlparser.catalog;


import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

@Slf4j
public abstract class AbstractCatalogDiscover implements ICatalogDiscover {
    protected final String defaultDatabase;
    protected final DataSourceDto dataSourceDto;

    public AbstractCatalogDiscover(String defaultDatabase, DataSourceDto dataSourceDto) {
        Preconditions.checkArgument(Objects.nonNull(dataSourceDto), "datasource cannot be null");
        this.defaultDatabase = defaultDatabase;
        this.dataSourceDto = dataSourceDto;
    }

    @Override
    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    @Override
    public void open() throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws BusinessException {

    }

    @Override
    public Set<String> listDatabases() throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> listTables(String s) throws BusinessException {
        throw new UnsupportedOperationException();
    }
}
