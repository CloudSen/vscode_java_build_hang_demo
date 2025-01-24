package cn.cisdigital.datakits.framework.model.dto.doris;


import cn.cisdigital.datakits.framework.model.dto.ParamDto;
import cn.cisdigital.datakits.framework.model.enums.DorisDataModelEnum;
import cn.cisdigital.datakits.framework.model.enums.PartitionModelEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xxx
 * @since 2024-03-14-9:57
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class DorisTableConfigDto implements Serializable {

    /**
     * doris建表模型 1.Aggregate  2.Unique   3.Duplicate
     */
    DorisDataModelEnum dataModel;
    /**
     * 1.默认（不分区） 2.自定义分区
     */
    PartitionModelEnum partitionMode = PartitionModelEnum.DEFAULT_PARTITION;
    /**
     * 分区配置
     */
    PartitionConfigDto partitionConfig;
    /**
     * 分桶配置
     */
    BucketConfigDto bucketConfig;
    /**
     * 其他配置参数
     */
    List<ParamDto> paramConfigList = new ArrayList<>();

    /**
     * 是否分区
     */
    public boolean isPartition() {
        return Objects.nonNull(partitionConfig) && Objects.nonNull(partitionConfig.getPartitionModel());
    }

    /**
     * 获取分区配置, 如果分区配置不存在, 则初始化分区配置, 注意: 如果不设置partitionConfig中的属性, 则不要进行初始化
     */
    public PartitionConfigDto getOrInitPartitionConfig() {
        if (Objects.isNull(partitionConfig)) {
            partitionConfig = new PartitionConfigDto();
        }
        return partitionConfig;
    }
}
