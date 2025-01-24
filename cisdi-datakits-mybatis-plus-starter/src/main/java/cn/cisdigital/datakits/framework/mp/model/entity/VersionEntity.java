package cn.cisdigital.datakits.framework.mp.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 乐观锁
 *
 * @author xxx
 * @since 2024-03-05
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class VersionEntity extends IdEntity implements Serializable {

    @TableField(value = "version", keepGlobalFormat = true, fill = FieldFill.INSERT_UPDATE)
    @Version
    private Long version;
}
