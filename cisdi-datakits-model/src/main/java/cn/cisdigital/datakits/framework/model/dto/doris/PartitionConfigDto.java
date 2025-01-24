package cn.cisdigital.datakits.framework.model.dto.doris;

import cn.cisdigital.datakits.framework.model.enums.DorisPartitionModelEnum;
import cn.cisdigital.datakits.framework.model.enums.PartitionTimeSpanEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxx
 * @since 2024-03-14-15:33
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class PartitionConfigDto implements Serializable {

    /**
     * 分区方式
     */
    DorisPartitionModelEnum partitionModel = DorisPartitionModelEnum.RANGE;
    /**
     * 分区字段
     */
    List<String> partitionColumnList = new ArrayList<>();
    /**
     * 分区粒度 1.每小时 2.每天 3.每周 4.每月 5每年
     */
    PartitionTimeSpanEnum timeSpan;

    /**
     * 是否创建历史分区
     */
    Boolean createHistoryPartition;

    /**
     * 初始化历史分区数量
     */
    Integer historyPartitionNum;

    /**
     * 分区保留策略
     */
    Integer partitionStart;
    /**
     * 提前创建N个分区/创建未来分区
     */
    Integer partitionEnd;
    /**
     * 分区前缀
     */
    String partitionPrefix;

}
