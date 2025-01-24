package cn.cisdigital.datakits.framework.mp.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 操作人
 *
 * @author xxx
 * @since 2024-03-05
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class OperatorEntity extends IdEntity implements Serializable {

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
}
