package cn.cisdigital.datakits.framework.model.dto.qbee.open;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author xxx
 * @since 2024-04-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDetailDto extends EmployeeDto implements Serializable {

    /**
     * 电话
     */
    String mobile;
    /**
     * 员工邮箱
     */
    String email;
    /**
     * 员工头像
     */
    String profile;
    /**
     * 员工工号
     */
    String employeeNo;
    /**
     * 员工所属主组织机构信息
     */
    OrgBaseDto orgInfo;

    public EmployeeDetailDto(String employeeId){
        this.employeeId = employeeId;
    }
}
