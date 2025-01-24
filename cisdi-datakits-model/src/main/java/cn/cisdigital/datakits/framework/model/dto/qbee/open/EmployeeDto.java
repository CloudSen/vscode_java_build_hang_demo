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
@FieldDefaults(level = AccessLevel.PROTECTED)
public class EmployeeDto implements Serializable {

    /**
     * 员工id
     */
    String employeeId;
    /**
     * 员工名称
     */
    String employeeName;
    /**
     * 账号
     */
    String account;


    public EmployeeDto(String employeeId){
        this.employeeId = employeeId;
    }
}
