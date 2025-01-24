package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * doris分桶方式
 *
 * @author xxx
 */
@Getter
@RequiredArgsConstructor
public enum DorisBucketType implements BaseEnum {

    /**
     *  使用指定的 key 列进行哈希分桶
     */
    HASH(1, "DISTRIBUTED BY HASH"),
    /**
     * 使用随机数进行分桶
     */
    RANDOM(2, "DISTRIBUTED BY RANDOM"),
    ;

    private final int code;
    /**
     * doris分桶方式的语法编码
     */
    private final String dorisCode;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

}
