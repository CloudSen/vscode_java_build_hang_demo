package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static cn.hutool.core.text.StrPool.DOT;

/**
 * @author xxx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewBase implements Serializable {

    /**
     * 数据库类型
     */
    @NotNull
    private DataBaseTypeEnum dataBaseTypeEnum;

    /**
     * MySQL、Doris 为数据库名
     * Oracle、DB2、PGSQL、SQL Server 为schema名
     */
    private String schema;

    /**
     * 操作的视图
     */
    @NotBlank
    private String viewName;

    public String getFullViewName() {
        String wrappedTableName = this.dataBaseTypeEnum.wrappedEscapeCharacter(this.viewName);
        return CharSequenceUtil.isNotBlank(this.schema) ?
            this.dataBaseTypeEnum.wrappedEscapeCharacter(this.schema) + DOT + wrappedTableName : wrappedTableName;
    }
}
