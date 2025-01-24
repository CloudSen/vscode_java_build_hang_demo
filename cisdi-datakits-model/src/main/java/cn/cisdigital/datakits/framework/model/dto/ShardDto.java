package cn.cisdigital.datakits.framework.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 分片对象
 *
 * @author xxx
 * @since 2024/6/22 10:46 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ShardDto {

    /**
     * 当前分片号,从0开始
     */
    int shardIndex;
    /**
     * 分片总数
     */
    int shardTotal;
}
