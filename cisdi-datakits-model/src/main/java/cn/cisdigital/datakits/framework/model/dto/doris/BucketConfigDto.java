package cn.cisdigital.datakits.framework.model.dto.doris;

import cn.cisdigital.datakits.framework.model.enums.DorisBucketType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxx
 * @since 2024-03-14-15:37
 */
@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class BucketConfigDto implements Serializable {

    /**
     * 分桶类型
     */
    DorisBucketType bucketType = DorisBucketType.HASH;
    /**
     * 分桶字段名
     */
    List<String> bucketColumnList = new ArrayList<>();
    /**
     * 分桶数
     */
    String bucketNum;
}
