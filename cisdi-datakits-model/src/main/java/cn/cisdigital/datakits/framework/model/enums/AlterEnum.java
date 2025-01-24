package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xxx
 * @since 2022-11-02-11:22
 * 对于数据库表的DDL alter选项的枚举
 */

@Getter
@AllArgsConstructor
public enum AlterEnum implements BaseEnum {

    //todo 当前版本支持增删列，change支持修改类型 修改名字 不支持其他修改
    /**
     * 增加列
     */
    ADD(1, "ADD"),
    /**
     * 删除列
     */
    DROP(2, "DROP"),
    /**
     * 对列名修改等CHANGE操作
     */
    CHANGE(3, "CHANGE"),
    /**
     * 对列内部属性修改，不涉及改列名
     */
    MODIFY(4, "MODIFY"),
    /**
     * 更新主键，逻辑为先删除主键 DROP PRIMARY KEY，再添加主键 ADD PRIMARY KEY
     */
    UPDATE_KEY(5, "ADD PRIMARY KEY"),
    ;
    private int index;
    private String keyword;

    @Override
    public int getCode() {
        return index;
    }

    @Override
    public String getKey() {
        return this.name();
    }

    @JsonCreator
    public static AlterEnum parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(AlterEnum.values()).filter(v -> v.getCode() == value).findFirst().orElse(null);
    }
}
