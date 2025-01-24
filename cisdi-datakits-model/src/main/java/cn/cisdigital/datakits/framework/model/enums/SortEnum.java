package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xxx
 * @since 2023-05-22-8:56
 */
@Getter
@AllArgsConstructor
public enum SortEnum implements BaseEnum {

    /**
     * 正序
     */
    ASC(1),
    /**
     * 逆序
     */
    DESC(2),
    /**
     * 不排序，维持原样
     */
    UNSORTED(3),
    ;

    final int value;

    @JsonCreator
    public static SortEnum parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(SortEnum.values()).filter(v -> v.getValue() == value).findFirst().orElse(null);
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
