package cn.cisdigital.datakits.framework.dynamic.datasource.utils;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import cn.cisdigital.datakits.framework.model.enums.ColumnTypeStrategyFactory;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.DataTypeEnum;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xxx
 * @since 2023-03-29-15:31
 */
@Slf4j
public class DatasourceUtils {

    /**
     * 解析各种数据库的列类型的分类类型 空指针则返回 null
     */
    public static DataTypeEnum parseColumnClassification(DataBaseTypeEnum dataBaseTypeEnum, String originColumnTypeName) {
        try {
            ColumnType columnType = parseColumnType(dataBaseTypeEnum, originColumnTypeName);
            return Objects.isNull(columnType) ? DataTypeEnum.OTHER_TYPE : columnType.getDataType();
        } catch (NullPointerException e) {
            log.error("未能查询到对应字符串列类型的分类枚举，数据库类型：" + dataBaseTypeEnum + "  原始列类型：" + originColumnTypeName, e);
            return DataTypeEnum.OTHER_TYPE;
        } catch (Exception e) {
            log.error("枚举类型转换异常，数据库类型：" + dataBaseTypeEnum + "  原始列类型：" + originColumnTypeName, e);
            return DataTypeEnum.OTHER_TYPE;
        }
    }

    /**
     * 解析各种数据库的列类型为枚举
     */
    public static ColumnType parseColumnType(DataBaseTypeEnum dataBaseTypeEnum, String columnTypeString) {
        ColumnType columnType = ColumnTypeStrategyFactory.listColumnType()
            .stream()
            .filter(type -> type.getDatabaseTypeEnum().equals(dataBaseTypeEnum))
            .findFirst()
            .orElse(null);
        return Objects.isNull(columnType) ? null : columnType.parseColumnType(columnTypeString);
    }
}
