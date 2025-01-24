package cn.cisdigital.datakits.framework.util.sqlparser.enums;

import cn.cisdigital.datakits.framework.model.enums.OperationTypeEnum;
import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 解析后：数据库操作枚举
 *
 * @author xxx
 * @since 2024-04-19-8:56
 */
@Getter
@AllArgsConstructor
public enum ParseOperationEnum implements BaseEnum {

    CREATE(1, OperationTypeEnum.CREATE),

    INSERT(11, OperationTypeEnum.UPDATE),
    UPDATE(12, OperationTypeEnum.UPDATE),
    DELETE(13, OperationTypeEnum.UPDATE),
    TRUNCATE(14, OperationTypeEnum.UPDATE),

    SELECT(21, OperationTypeEnum.READ),
    USE(22, OperationTypeEnum.READ),
    SHOW(23, OperationTypeEnum.READ),

    ALTER(31, OperationTypeEnum.ALTER),

    DROP(41, OperationTypeEnum.DROP),


    /**
     * 其他
     */
    OTHERS(9999, OperationTypeEnum.OTHERS),
    ;

    private final int value;

    /**
     * 映射数据访问控制定义的增删改查权限
     */
    private final OperationTypeEnum dataControlOperationType;

    @JsonCreator
    public static ParseOperationEnum parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(ParseOperationEnum.values()).filter(v -> v.getValue() == value).findFirst().orElse(null);
    }

    @Override
    public int getCode() {
        return value;
    }

    @Override
    public String getKey() {
        return this.name();
    }
}
