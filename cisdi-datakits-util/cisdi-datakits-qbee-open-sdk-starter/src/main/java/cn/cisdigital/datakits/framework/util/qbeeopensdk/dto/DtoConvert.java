package cn.cisdigital.datakits.framework.util.qbeeopensdk.dto;

import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDetailDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgBaseDto;
import im.qingtui.qbee.open.platfrom.base.model.vo.base.BaseInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeInfo;

import java.util.Objects;

/**
 * @author xxx
 * @date 2024-07-12-9:56
 */
public class DtoConvert {


    public static EmployeeDto convertEmployeeDto(EmployeeInfo employeeInfo) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeId(employeeInfo.getId());
        employeeDto.setEmployeeName(employeeInfo.getName());
        employeeDto.setAccount(employeeInfo.getAccount());
        return employeeDto;
    }

    public static EmployeeDetailDto convertEmployeeDetailDto(EmployeeInfo employeeInfo) {
        EmployeeDetailDto employeeDetailDto = new EmployeeDetailDto();
        employeeDetailDto.setMobile(employeeInfo.getMobile());
        employeeDetailDto.setEmail(employeeInfo.getEmail());
        employeeDetailDto.setProfile(employeeInfo.getProfile());
        employeeDetailDto.setEmployeeNo(employeeInfo.getEmployeeNo());
        employeeDetailDto.setEmployeeId(employeeInfo.getId());
        employeeDetailDto.setEmployeeName(employeeInfo.getName());
        employeeDetailDto.setAccount(employeeInfo.getAccount());
        BaseInfo org = employeeInfo.getOrg();
        if (Objects.nonNull(org)) {
            OrgBaseDto orgBaseDto = new OrgBaseDto(org.getId(), org.getName());
            employeeDetailDto.setOrgInfo(orgBaseDto);
        }
        return employeeDetailDto;
    }
}
