package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;

import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDetailDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgBaseDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgDto;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.dto.DtoConvert;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.excption.OpenSdkErrorCode;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.excption.OpenSdkException;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author xxx
 * @date 2024-07-09-10:11
 */
@Slf4j
public class EmployeeCacheService {


    /**
     * 获取Employee基本信息
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static Optional<EmployeeDto> getEmployeeById(String employeeId) {
        if (!StringUtils.hasText(employeeId)) {
            log.error("查询参数，employeeId为空。");
            return Optional.empty();
        }
        Optional<EmployeeInfo> employee = PlatformEmployeeManager.getEmployeeById(employeeId);
        return employee.map(DtoConvert::convertEmployeeDto);
    }

    /**
     * 获取Employee基本信息 查询不到抛异常
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static EmployeeDto getEmployeeByIdWithException(String employeeId) {
        Optional<EmployeeDto> employeeById = getEmployeeById(employeeId);
        if (!employeeById.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND, employeeId);
        }
        return employeeById.get();
    }

    /**
     * 获取Employee基本信息 查询不到填充默认值
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static EmployeeDto getEmployeeByIdWithDefault(String employeeId) {
        Optional<EmployeeDto> employeeById = getEmployeeById(employeeId);
        return employeeById.orElseGet(() -> new EmployeeDto(employeeId));
    }


    /**
     * 获取Employee详细信息
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static Optional<EmployeeDetailDto> getEmployeeDetailById(String employeeId) {
        Optional<EmployeeInfo> employee = PlatformEmployeeManager.getEmployeeById(employeeId);
        return employee.map(DtoConvert::convertEmployeeDetailDto);
    }

    /**
     * 获取Employee详细信息  查询不到抛异常
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static EmployeeDetailDto getEmployeeDetailByIdWithException(String employeeId) {
        Optional<EmployeeDetailDto> employeeDetailById = getEmployeeDetailById(employeeId);
        if (!employeeDetailById.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND, employeeId);
        }
        return employeeDetailById.get();
    }

    /**
     * 获取Employee详细信息  查询不到填充默认值
     *
     * @param employeeId 员工Id
     * @return: 员工信息
     */
    public static EmployeeDetailDto getEmployeeDetailByIdWithDefault(String employeeId) {
        Optional<EmployeeDetailDto> employeeDetailById = getEmployeeDetailById(employeeId);
        return employeeDetailById.orElseGet(() -> new EmployeeDetailDto(employeeId));
    }


    /**
     * 批量获取用户基本信息
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, Optional<EmployeeDto>> getEmployeeBatch(Collection<String> employeeIdList) {
        Map<String, Optional<EmployeeDto>> result = new HashMap<>(0);
        Map<String, Optional<EmployeeInfo>> employeeByIds = PlatformEmployeeManager.getEmployeeByIds(employeeIdList);
        employeeByIds.forEach((k, v) -> {
            Optional<EmployeeDto> employeeDto = v.map(DtoConvert::convertEmployeeDto);
            result.put(k, employeeDto);
        });
        return result;
    }

    /**
     * 批量获取用户基本信息 如果存在一个或者某个用户未能查询到，抛异常
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, EmployeeDto> getEmployeeBatchWithException(Collection<String> employeeIdList) {
        Map<String, EmployeeDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDto>> employeeBatch = getEmployeeBatch(employeeIdList);
        employeeBatch.forEach((k, v) -> {
            if (!v.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND, k);
            }
            result.put(k, v.get());
        });
        return result;
    }

    /**
     * 批量获取用户基本信息 如果查询不到用户，填充默认值
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, EmployeeDto> getEmployeeBatchWithDefault(Collection<String> employeeIdList) {
        Map<String, EmployeeDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDto>> employeeBatch = getEmployeeBatch(employeeIdList);
        employeeBatch.forEach((k, v) -> {
            if (v.isPresent()) {
                result.put(k, v.get());
            } else {
                result.put(k, new EmployeeDto(k));
            }
        });
        return result;
    }

    /**
     * 批量获取用户详细信息
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, Optional<EmployeeDetailDto>> getEmployeeDetailBatch(Collection<String> employeeIdList) {
        Map<String, Optional<EmployeeDetailDto>> result = new HashMap<>(0);
        Map<String, Optional<EmployeeInfo>> employeeByIds = PlatformEmployeeManager.getEmployeeByIds(employeeIdList);
        employeeByIds.forEach((k, v) -> {
            Optional<EmployeeDetailDto> employeeDetailDto = v.map(DtoConvert::convertEmployeeDetailDto);
            result.put(k, employeeDetailDto);
        });
        return result;
    }

    /**
     * 批量获取用户详细信息 如果存在一个或者某个用户未能查询到，抛异常
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, EmployeeDetailDto> getEmployeeDetailBatchWithException(Collection<String> employeeIdList) {
        Map<String, EmployeeDetailDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDetailDto>> employeeDetailBatch = getEmployeeDetailBatch(employeeIdList);
        employeeDetailBatch.forEach((k, v) -> {
            if (!v.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND, k);
            }
            result.put(k, v.get());
        });
        return result;
    }

    /**
     * 批量获取用户详细信息 如果查询不到用户，填充默认值
     *
     * @param employeeIdList
     * @return
     */
    public static Map<String, EmployeeDetailDto> getEmployeeDetailBatchWithDefault(Collection<String> employeeIdList) {
        Map<String, EmployeeDetailDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDetailDto>> employeeDetailBatch = getEmployeeDetailBatch(employeeIdList);
        employeeDetailBatch.forEach((k, v) -> {
            if (v.isPresent()) {
                result.put(k, v.get());
            } else {
                result.put(k, new EmployeeDetailDto(k));
            }
        });
        return result;
    }

    /**
     * 获取用户主组织机构信息
     *
     * @param employeeId
     * @return
     */
    public static Optional<OrgDto> getEmployeeMasterOrg(String employeeId) {
        Optional<EmployeeDetailDto> employeeDetailById = getEmployeeDetailById(employeeId);
        if (!employeeDetailById.isPresent()) {
            return Optional.empty();
        }
        EmployeeDetailDto employeeDetailDto = employeeDetailById.get();
        if (employeeDetailDto.getOrgInfo() == null || employeeDetailDto.getOrgInfo().getOrgId() == null) {
            return Optional.empty();
        }
        return OrgCacheService.getOrgById(employeeDetailDto.getOrgInfo().getOrgId());
    }

    /**
     * 获取用户主组织机构信息
     *
     * @param employeeId
     * @return
     */
    public static OrgDto getEmployeeMasterOrgWithException(String employeeId) {
        Optional<OrgDto> employeeMasterOrg = getEmployeeMasterOrg(employeeId);
        if (!employeeMasterOrg.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.MASTER_ORG_IS_NOT_FOUND, employeeId);
        }
        return employeeMasterOrg.get();
    }

    /**
     * 批量获取用户主组织机构信息
     *
     * @return
     */
    public static Map<String, Optional<OrgDto>> getEmployeeMasterOrgBatch(Collection<String> employeeIdList) {
        Map<String, Optional<OrgDto>> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDetailDto>> employeeDetailBatch = getEmployeeDetailBatch(employeeIdList);
        employeeDetailBatch.forEach((k, v) -> {
            if (!v.isPresent()) {
                result.put(k, Optional.empty());
                return;
            }
            EmployeeDetailDto employeeDetailDto = v.get();
            if (employeeDetailDto.getOrgInfo() == null || employeeDetailDto.getOrgInfo().getOrgId() == null) {
                result.put(k, Optional.empty());
                return;
            }
            Optional<OrgDto> orgById = OrgCacheService.getOrgById(employeeDetailDto.getOrgInfo().getOrgId());
            result.put(k, orgById);
        });
        return result;
    }

    /**
     * 批量获取用户主组织机构信息
     *
     * @return
     */
    public static Map<String, OrgDto> getEmployeeMasterOrgBatchWithException(Collection<String> employeeIdList) {
        Map<String, OrgDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDetailDto>> employeeDetailBatch = getEmployeeDetailBatch(employeeIdList);
        employeeDetailBatch.forEach((k, v) -> {
            if (!v.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.MASTER_ORG_IS_NOT_FOUND, k);
            }
            EmployeeDetailDto employeeDetailDto = v.get();
            if (employeeDetailDto.getOrgInfo() == null || employeeDetailDto.getOrgInfo().getOrgId() == null) {
                throw new OpenSdkException(OpenSdkErrorCode.MASTER_ORG_IS_NOT_FOUND, k);
            }
            Optional<OrgDto> orgById = OrgCacheService.getOrgById(employeeDetailDto.getOrgInfo().getOrgId());
            if (!orgById.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.MASTER_ORG_IS_NOT_FOUND, k);
            }
            result.put(k, orgById.get());
        });
        return result;
    }

    /**
     * 获取一个用户的所有组织,以及该组织的上游组织id
     *
     * @param employeeId 用户id
     * @return 组织路径
     * @throws Exception 调用方需要自行处理平台异常
     */
    public static Set<String> getAllOrgAndSuperiorOrgIdOfEmployee(String employeeId) throws Exception {
        Set<String> result = new HashSet<>();
        List<OrgDto> orgDtoList = getAllOrgByEmployeeId(employeeId);
        for (OrgDto orgDto : orgDtoList) {
            result.add(orgDto.getOrgId());
            List<OrgBaseDto> orgPathList = orgDto.getOrgPathList();
            if (!CollectionUtils.isEmpty(orgPathList)) {
                for (OrgBaseDto orgBaseDto : orgPathList) {
                    result.add(orgBaseDto.getOrgId());
                }
            }
        }
        return result;
    }

    /**
     * 判断用户是否属于指定组织机构
     *
     * @param employeeId 用户id
     * @param orgId      机构id
     * @return 用户是否属于该组织机构
     * @throws Exception 调用方需要自行处理平台异常
     */
    public static boolean checkEmployeeBelongOrg(String employeeId, String orgId) throws Exception {
        Set<String> allOrgIds = getAllOrgAndSuperiorOrgIdOfEmployee(employeeId);
        return allOrgIds.contains(orgId);
    }


    /**
     * 获取一个用户的所有组织
     *
     * @param employeeId 用户id
     * @return 组织路径
     * @throws Exception 调用方需要自行处理平台异常
     */
    public static List<OrgDto> getAllOrgByEmployeeId(String employeeId) throws Exception {
        List<OrgDto> result = new ArrayList<>();
        List<String> allOrgIdList = PlatformEmployeeManager.getAllOrgByEmployId(employeeId);
        OrgCacheService.getOrgBatch(allOrgIdList).forEach((k, v) -> v.ifPresent(result::add));
        return result;
    }

    /**
     * 通过UserId获取Employee基本信息
     *
     * @param userId 用户Id
     * @return: 员工信息
     */
    public static Optional<EmployeeDto> getEmployeeByUserId(String userId) {
        Optional<EmployeeInfo> employee = PlatformEmployeeManager.getEmployeeByUserId(userId);
        return employee.map(DtoConvert::convertEmployeeDto);
    }

    /**
     * 通过UserId获取Employee基本信息
     *
     * @param userId 用户Id
     * @return: 员工信息
     */
    public static EmployeeDto getEmployeeByUserIdWithException(String userId) {
        Optional<EmployeeDto> employeeById = getEmployeeByUserId(userId);
        if (!employeeById.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND_BY_USER_ID, userId);
        }
        return employeeById.get();
    }

    /**
     * 通过UserId批量获取Employee基本信息
     *
     * @param userIds 批量用户Id
     * @return: 员工信息
     */
    public static Map<String, Optional<EmployeeDto>> getEmployeeByUserIdBatch(Collection<String> userIds) {
        Map<String, Optional<EmployeeDto>> result = new HashMap<>();
        Map<String, Optional<EmployeeInfo>> employeeByUserIds = PlatformEmployeeManager.getEmployeeByUserIds(userIds);
        employeeByUserIds.forEach((k, v) -> {
            result.put(k, v.map(DtoConvert::convertEmployeeDto));
        });
        return result;
    }

    /**
     * 通过UserId批量获取Employee基本信息
     * 如果查询不出userId对应的Employee基本信息，将返回空对象
     *
     * @param userIds 批量用户Id
     * @return: 员工信息
     */
    public static Map<String, EmployeeDto> getEmployeeByUserIdBatchWithDefault(Collection<String> userIds) {
        Map<String, EmployeeDto> result = new HashMap<>();
        Map<String, Optional<EmployeeInfo>> employeeByUserIds = PlatformEmployeeManager.getEmployeeByUserIds(userIds);
        employeeByUserIds.forEach((k, v) -> {
            if(v.isPresent()){
                result.put(k, DtoConvert.convertEmployeeDto(v.get()));
            }else {
                result.put(k, new EmployeeDto());
            }
        });
        return result;
    }

    /**
     * 通过UserId批量获取Employee基本信息
     * 如果任一一个userId查询不出对应的Employee基本信息，将会报错
     *
     * @param userIds 批量用户Id
     * @return: 员工信息
     */
    public static Map<String, EmployeeDto> getEmployeeByUserIdBatchWithException(Collection<String> userIds) {
        Map<String, EmployeeDto> result = new HashMap<>();
        Map<String, Optional<EmployeeInfo>> employeeByUserIds = PlatformEmployeeManager.getEmployeeByUserIds(userIds);
        employeeByUserIds.forEach((k, v) -> {
            if(v.isPresent()){
                result.put(k, DtoConvert.convertEmployeeDto(v.get()));
            }else {
                throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND_BY_USER_ID, k);
            }
        });
        return result;
    }


    /**
     * 通过account获取Employee基本信息
     *
     * @param account 账户Id
     * @return: 员工信息
     */
    public static Optional<EmployeeDto> getEmployeeByAccount(String account) {
        Optional<EmployeeInfo> employee = PlatformEmployeeManager.getEmployeeByAccount(account);
        return employee.map(DtoConvert::convertEmployeeDto);
    }

    /**
     * 通过account获取Employee基本信息
     *
     * @param account 账户Id
     * @return: 员工信息
     */
    public static EmployeeDto getEmployeeByAccountWithException(String account) {
        Optional<EmployeeDto> employeeById = getEmployeeByAccount(account);
        if (!employeeById.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND_BY_ACCOUNT, account);
        }
        return employeeById.get();
    }

    /**
     * 通过account获取Employee基本信息 没有返回默认Dto
     *
     * @param account 账户Id
     * @return: 员工信息
     */
    public static EmployeeDto getEmployeeByAccountWithDefault(String account) {
        Optional<EmployeeDto> employeeById = getEmployeeByAccount(account);
        if (!employeeById.isPresent()) {
            EmployeeDto dto = new EmployeeDto();
            dto.setAccount(account);
            return dto;
        }
        return employeeById.get();
    }

    /**
     * 通过account 批量获取Employee基本信息
     *
     * @param accountList 账户Id
     * @return: 员工信息
     */
    public static Map<String, Optional<EmployeeDto>> getEmployeeByAccountBatch(Collection<String> accountList) {
        Map<String, Optional<EmployeeDto>> result = new HashMap<>(0);
        Map<String, Optional<EmployeeInfo>> employeeByAccountBatch = PlatformEmployeeManager.getEmployeeByAccountBatch(accountList);
        employeeByAccountBatch.forEach((k, v) -> {
            Optional<EmployeeDto> employeeDto = v.map(DtoConvert::convertEmployeeDto);
            result.put(k, employeeDto);
        });
        return result;
    }

    /**
     * 通过账户 批量获取用户基本信息 如果存在一个或者某个用户未能查询到，抛异常
     *
     * @param accountList
     * @return
     */
    public static Map<String, EmployeeDto> getEmployeeByAccountBatchWithException(Collection<String> accountList) {
        Map<String, EmployeeDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDto>> employeeBatch = getEmployeeByAccountBatch(accountList);
        employeeBatch.forEach((k, v) -> {
            if (!v.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.THIS_EMPLOYEE_IS_NOT_FOUND_BY_ACCOUNT, k);
            }
            result.put(k, v.get());
        });
        return result;
    }

    /**
     * 通过账户 批量获取用户基本信息 如果查询不到用户，填充默认值
     *
     * @param accountList
     * @return
     */
    public static Map<String, EmployeeDto> getEmployeeByAccountBatchWithDefault(Collection<String> accountList) {
        Map<String, EmployeeDto> result = new HashMap<>(0);
        Map<String, Optional<EmployeeDto>> employeeBatch = getEmployeeByAccountBatch(accountList);
        employeeBatch.forEach((k, v) -> {
            if (v.isPresent()) {
                result.put(k, v.get());
            } else {
                EmployeeDto dto = new EmployeeDto();
                dto.setAccount(k);
                result.put(k, dto);
            }
        });
        return result;
    }

}
