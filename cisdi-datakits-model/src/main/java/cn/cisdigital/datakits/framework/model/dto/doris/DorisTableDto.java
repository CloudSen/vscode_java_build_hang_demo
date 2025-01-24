package cn.cisdigital.datakits.framework.model.dto.doris;

import cn.cisdigital.datakits.framework.model.dto.database.ColumnBase;
import cn.cisdigital.datakits.framework.model.dto.database.IndexDto;
import cn.cisdigital.datakits.framework.model.enums.MaterializeTypeEnum;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Doris 表信息
 *
 * @author xxx
 */
@Data
public class DorisTableDto implements Serializable {

    private static final long serialVersionUID = -2541384911604948692L;
    /**
     * 数据库名
     */
    private String databaseName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表类型
     */
    private MaterializeTypeEnum tableType;
    /**
     * 列信息
     */
    private List<ColumnBase> columnList = new ArrayList<>();
    /**
     * 数据行数
     */
    private long tableRows;
    /**
     * 数据长度，单位：B
     */
    private long dataLength;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 备注
     */
    private String tableComment;
    /**
     * 高级配置
     */
    private DorisTableConfigDto tableConfig;

    /**
     * 索引信息
     */
    private List<IndexDto> indexList = Lists.newArrayList();
}
