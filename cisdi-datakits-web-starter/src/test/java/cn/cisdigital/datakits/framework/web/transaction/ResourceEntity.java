package cn.cisdigital.datakits.framework.web.transaction;

import cn.cisdigital.datakits.framework.mp.model.entity.OperatorAndTimeEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *@Description：资源表
 *@author xxx
 *@CreateDate: 2023/5/17 19:00
 *@Version: 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("common_resource")
public class ResourceEntity extends OperatorAndTimeEntity implements Serializable {

    private static final long serialVersionUID = -4107319559161453335L;

    /**
     * 别名
     */
    @TableField(value = "alias", keepGlobalFormat = true)
    private String alias;

    /**
     * 资源名
     */
    @TableField(value = "file_name", keepGlobalFormat = true)
    private String fileName;

    /**
     * 描述信息
     */
    @TableField(value = "description", keepGlobalFormat = true)
    private String description;

    /**
     * 资源大小
     */
    @TableField(value = "size", keepGlobalFormat = true)
    private Long size;

    /**
     * 资源全路径
     */
    @TableField(value = "full_name", keepGlobalFormat = true)
    private String fullName;

    @TableField(value = "mime_type", keepGlobalFormat = true)
    private String mimeType;

    /**
     * 资源后缀
     */
    @TableField(value = "suffix", keepGlobalFormat = true)
    private String suffix;

    /**
     * 资源类型：1-java 、2-python 、3-scala 、4-udf 、5-txt、6-png、7-pdf
     */
    @TableField(value = "type", keepGlobalFormat = true)
    private ResourceType type;

    /**
     * 资源协议：1-hdfs、2-minio
     */
    @TableField(value = "scheme", keepGlobalFormat = true)
    private ResourceScheme scheme;

    /**
     * 目录ID
     */
    @TableField(value = "dir_id", keepGlobalFormat = true)
    private Long dirId;

    @TableField(
        value = "version",
        keepGlobalFormat = true,
        fill = FieldFill.INSERT_UPDATE
    )
    @Version
    private Long version;

}
