package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-08-09-9:45
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SourceType implements BaseEnum {

    /**
     * ALL
     */
    ALL(0),
    /**
     * DATABASE
     */
    DATABASE(1),
    /**
     * MQ
     */
    MQ(2),
    /**
     * API
     */
    API(3),
    /**
     * SAP
     */
    SAP(4),
    ;

    /**
     * 枚举值
     */
    final int value;

    @JsonCreator
    public static SourceType parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(SourceType.values()).filter(type -> type.getValue() == value).findFirst().orElse(null);
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
