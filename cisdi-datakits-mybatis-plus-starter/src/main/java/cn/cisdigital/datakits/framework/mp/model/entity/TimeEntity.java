package cn.cisdigital.datakits.framework.mp.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建时间和更新时间
 *
 * @author xxx
 * @since 2024-03-05
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class TimeEntity extends IdEntity implements Serializable {

    @TableField(value = "create_time", keepGlobalFormat = true, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", keepGlobalFormat = true, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
