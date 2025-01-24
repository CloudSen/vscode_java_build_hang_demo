package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;

import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgBaseDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgDto;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.excption.OpenSdkErrorCode;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.excption.OpenSdkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author xxx
 * @date 2024-07-09-10:11
 */
@Slf4j
public class OrgCacheService {

    /**
     * 根据组织id批量查询组织，及其全路径
     *
     * @param orgId 组织id
     * @return 机构信息
     */
    public static Optional<OrgDto> getOrgById(String orgId) {
        if(!StringUtils.hasText(orgId)){
            log.error("查询参数，orgId为空。");
            return Optional.empty();
        }
        return PlatformOrgManager.getOrgById(orgId);
    }

    /**
     * 根据组织id批量查询组织，及其全路径 如果查询不到，抛异常
     *
     * @param orgId 组织id
     * @return 机构信息
     */
    public static OrgDto getOrgByIdWithException(String orgId) {
        Optional<OrgDto> orgById = PlatformOrgManager.getOrgById(orgId);
        if (!orgById.isPresent()) {
            throw new OpenSdkException(OpenSdkErrorCode.THIS_ORG_IS_NOT_FOUND, orgId);
        }
        return orgById.get();
    }

    /**
     * 根据组织id批量查询组织，及其全路径 如果查询不到，抛异常
     *
     * @param orgId 组织id
     * @return 机构信息
     */
    public static OrgDto getOrgByIdWithDefault(String orgId) {
        Optional<OrgDto> orgById = PlatformOrgManager.getOrgById(orgId);
        return orgById.orElseGet(() -> new OrgDto(orgId));
    }


    /**
     * 根据组织id批量查询组织，及其全路径
     *
     * @param orgIdList 组织id
     * @return 机构信息
     */
    public static Map<String, Optional<OrgDto>> getOrgBatch(Collection<String> orgIdList) {
        return PlatformOrgManager.getOrgByIds(orgIdList);
    }

    /**
     * 根据组织id批量查询组织，及其全路径 如果有一个org查询不到，报错
     *
     * @param orgIdList 组织id
     * @return 机构信息
     */
    public static Map<String, OrgDto> getOrgBatchWithException(Collection<String> orgIdList) {
        Map<String, OrgDto> orgMap = new HashMap<>(0);
        Map<String, Optional<OrgDto>> orgByIds = PlatformOrgManager.getOrgByIds(orgIdList);
        orgByIds.forEach((k, v) -> {
            if (!v.isPresent()) {
                throw new OpenSdkException(OpenSdkErrorCode.THIS_ORG_IS_NOT_FOUND, k);
            }
            orgMap.put(k, v.get());
        });
        return orgMap;
    }

    /**
     * 根据组织id批量查询组织，及其全路径 如果有一个org查询不到，报错
     *
     * @param orgIdList 组织id
     * @return 机构信息
     */
    public static Map<String, OrgDto> getOrgBatchWithDefault(Collection<String> orgIdList) {
        Map<String, OrgDto> orgMap = new HashMap<>(0);
        Map<String, Optional<OrgDto>> orgByIds = PlatformOrgManager.getOrgByIds(orgIdList);
        orgByIds.forEach((k, v) -> {
            if (v.isPresent()) {
                orgMap.put(k, v.get());
            } else {
                orgMap.put(k, new OrgDto(k));
            }
        });
        return orgMap;
    }

    /**
     * 返回组织机构全路径
     *
     * @param orgId
     * @return
     */
    public static String getOrgPath(String orgId) {
        Optional<OrgDto> orgById = getOrgById(orgId);
        if (orgById.isPresent()) {
            List<OrgBaseDto> orgPathList = orgById.get().getOrgPathList();
            if (CollectionUtils.isEmpty(orgPathList)) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            orgPathList.forEach(p -> stringBuilder.append("/").append(p.getOrgName()));
            stringBuilder.deleteCharAt(0);
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * 批量返回组织机构全路径
     *
     * @param orgIdList
     * @return
     */
    public static Map<String, String> getOrgPathBatch(Collection<String> orgIdList) {
        Map<String, String> result = new HashMap<>(0);
        Map<String, Optional<OrgDto>> orgBatchMap = getOrgBatch(orgIdList);
        if (CollectionUtils.isEmpty(orgBatchMap)) {
            return result;
        }
        orgBatchMap.forEach((k, v) -> {
            StringBuilder stringBuilder = new StringBuilder();
            if(v.isPresent()){
                List<OrgBaseDto> orgPathList = v.get().getOrgPathList();
                if(!CollectionUtils.isEmpty(orgPathList)){
                    orgPathList.forEach(p -> stringBuilder.append("/").append(p.getOrgName()));
                    stringBuilder.deleteCharAt(0);
                    result.put(k, stringBuilder.toString());
                }else {
                    result.put(k, "");
                }
            }else {
                result.put(k, "");
            }
        });
        return result;
    }

    /**
     * 根据组织path查询组织
     *
     * @param path 组织路径
     * @return 机构信息
     */
    public static Optional<OrgDto> getOrgByPath(String path) {
        if(!StringUtils.hasText(path)){
            log.info("查询参数，path为空。");
            return Optional.empty();
        }
        return PlatformOrgManager.getOrgByPath(path);
    }

}
