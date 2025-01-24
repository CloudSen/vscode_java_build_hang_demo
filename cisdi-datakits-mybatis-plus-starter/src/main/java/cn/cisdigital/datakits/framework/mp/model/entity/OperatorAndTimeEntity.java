package cn.cisdigital.datakits.framework.mp.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作人和时间
 *
 * @author xxx
 * @since 2024-03-11
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class OperatorAndTimeEntity extends IdEntity implements Serializable {

    /**
     * 创建人，员工id
     */
    @TableField(value = "create_by", keepGlobalFormat = true, fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人，员工id
     */
    @TableField(value = "update_by", keepGlobalFormat = true, fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(value = "create_time", keepGlobalFormat = true, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", keepGlobalFormat = true, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
