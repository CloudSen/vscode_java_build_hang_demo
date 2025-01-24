package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;


import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgBaseDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import im.qingtui.qbee.open.platfrom.base.model.vo.base.BaseList;
import im.qingtui.qbee.open.platfrom.portal.model.param.org.OrgAllPathParam;
import im.qingtui.qbee.open.platfrom.portal.model.param.org.OrgPathParam;
import im.qingtui.qbee.open.platfrom.portal.model.vo.dimension.DimensionInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.org.OrgBaseInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.org.OrgChildrenVO;
import im.qingtui.qbee.open.platfrom.portal.service.DimensionService;
import im.qingtui.qbee.open.platfrom.portal.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024-04-28
 */
@Slf4j
@Component
public class PlatformOrgManager {

    /**
     * 组织机构维度枚举
     */
    private static final Integer ORG_DIMENSION_TYPE = 1;
    /**
     * key：机构Id     value：组织机构及其上级路径
     */
    private static LoadingCache<String, Optional<OrgDto>> ORG_CACHE;
    /**
     * key：机构路径 eg:/组织机构维度/中冶赛迪/赛迪信息     value：组织机构id
     */
    private static LoadingCache<String, Optional<String>> ORG_PATH_CACHE;
    /**
     * 组织机构的维度id
     */
    private static String DIMENSION_ID;

    @Value("${public.qbee-open-platform.cache-timeout-second:300}")
    private int qbeeCacheTimeoutSecond;

    @PostConstruct
    public void init() {
        log.info("qbee开放接口组织机构请求缓存过期时长: {} 秒", qbeeCacheTimeoutSecond);
        ORG_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(qbeeCacheTimeoutSecond, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, Optional<OrgDto>>() {
                    @Nonnull
                    @Override
                    public Optional<OrgDto> load(@Nonnull String orgId) {
                        try {
                            return addCache(orgId);
                        } catch (Exception e) {
                            log.error("请求机构信息失败：{}", orgId, e);
                        }
                        return Optional.empty();
                    }
                });

        ORG_PATH_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(qbeeCacheTimeoutSecond, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, Optional<String>>() {
                    @Nonnull
                    @Override
                    public Optional<String> load(@Nonnull String path) {
                        try {
                            return addPathCache(path);
                        } catch (Exception e) {
                            log.error("请求机构信息失败：{}", path, e);
                        }
                        return Optional.empty();
                    }
                });
    }

    /**
     * 按orgId获取组织机构
     */
    protected static Optional<OrgDto> getOrgById(String orgId) {
        try {
            return ORG_CACHE.get(orgId);
        } catch (ExecutionException e) {
            log.error("获取组织信息异常, orgId:{}", orgId, e);
        }
        return Optional.empty();
    }

    /**
     * 按orgIds批量获取组织机构
     */
    protected static Map<String, Optional<OrgDto>> getOrgByIds(Collection<String> orgIds) {
        Map<String, Optional<OrgDto>> result = new HashMap<>(0);
        if (CollectionUtils.isEmpty(orgIds)) {
            return result;
        }
        //需要批量加入缓存
        List<String> noCacheList = orgIds.stream()
            .filter(orgId -> StringUtils.hasText(orgId) && Objects.isNull(ORG_CACHE.getIfPresent(orgId)))
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(noCacheList)) {
            addCacheBatch(noCacheList);
        }
        for (String orgId : orgIds) {
            //兼容查询不出的错误
            Optional<OrgDto> orgDto;
            try {
                orgDto = ORG_CACHE.get(orgId);
            } catch (Exception e) {
                log.error("获取组织信息异常, orgId:{}", orgId, e);
                orgDto = Optional.empty();
            }
            result.put(orgId, orgDto);
        }
        return result;
    }

    /**
     * 按path获取组织机构
     */
    protected static Optional<OrgDto> getOrgByPath(String path) {
        try {
            Optional<String> orgId = ORG_PATH_CACHE.get(path);
            if (orgId.isPresent()) {
                return ORG_CACHE.get(orgId.get());
            } else {
                log.info("未能通过组织路径获取到组织信息, path:{}", path);
                return Optional.empty();
            }
        } catch (ExecutionException e) {
            log.error("通过组织路径获取组织信息异常, path:{}", path, e);
        }
        return Optional.empty();
    }

    /**
     * Qbee 获取组织机构
     */
    private static Optional<OrgDto> addCache(String orgId) {
        //查询组织机构路径
        OrgAllPathParam pathParam = new OrgAllPathParam();
        pathParam.setOrgParam(Collections.singletonList(orgId));
        pathParam.setParamType(1);
        List<OrgChildrenVO> orgAllPathList = null;
        try {
            orgAllPathList = OrgService.getOrgAllPathList(pathParam);
        } catch (Exception e) {
            log.error("获取组织全路径信息异常, orgId:{}", orgId, e);
            return Optional.empty();
        }
        if (CollectionUtils.isEmpty(orgAllPathList)) {
            return Optional.empty();
        }
        OrgChildrenVO childrenVo = orgAllPathList.get(0);
        OrgDto orgDto = buildOrgDto(childrenVo);
        //更新path缓存
        if (StringUtils.hasText(orgDto.getPath())) {
            ORG_PATH_CACHE.put(orgDto.getPath(), Optional.ofNullable(orgDto.getOrgId()));
        }
        return Optional.of(orgDto);
    }

    /**
     * Qbee 批量获取组织机构
     */
    private static void addCacheBatch(List<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return;
        }
        //查询组织机构路径
        OrgAllPathParam pathParam = new OrgAllPathParam();
        pathParam.setOrgParam(orgIds);
        pathParam.setParamType(1);
        List<OrgChildrenVO> orgAllPathList = null;
        try {
            orgAllPathList = OrgService.getOrgAllPathList(pathParam);
        } catch (Exception e) {
            log.error("批量查询组织机构失败，orgIds：{}", orgIds, e);
            return;
        }
        if (CollectionUtils.isEmpty(orgAllPathList)) {
            log.error("未能找到指定组织机构的路径信息，orgIds：{}", orgIds);
            return;
        }
        for (OrgChildrenVO vo : orgAllPathList) {
            OrgDto orgDto = buildOrgDto(vo);
            //更新path缓存
            if (StringUtils.hasText(orgDto.getPath())) {
                ORG_PATH_CACHE.put(orgDto.getPath(), Optional.ofNullable(orgDto.getOrgId()));
            }
            ORG_CACHE.put(orgDto.getOrgId(), Optional.of(orgDto));
        }
    }

    /**
     * Qbee 解析path
     */
    private static Optional<String> addPathCache(String path) {
        if (!StringUtils.hasText(DIMENSION_ID)) {
            initDimensionId();
        }
        OrgPathParam orgPathParam = new OrgPathParam();
        orgPathParam.setDimensionId(DIMENSION_ID);
        orgPathParam.setRootPath(path);
        BaseList<OrgBaseInfo> orgListByOrgPath = null;
        try {
            orgListByOrgPath = OrgService.getOrgListByOrgPath(orgPathParam);
        } catch (Exception e) {
            log.error("获取组织机构维度失败，无法通过路径解析组织机构...", e);
            return Optional.empty();
        }
        List<OrgBaseInfo> list = orgListByOrgPath.getList();
        if (!CollectionUtils.isEmpty(list)) {
            //最后一个是当前组织机构
            int lastIndex = list.size() - 1;
            OrgBaseInfo orgBaseInfo = list.get(lastIndex);
            return Optional.ofNullable(orgBaseInfo.getId());
        } else {
            log.error("获取组织机构维度失败，无法通过路径解析组织机构...");
            return Optional.empty();
        }
    }

    private static void initDimensionId() {
        //初始化组织机构维度id
        BaseList<DimensionInfo> dimensionList = null;
        try {
            dimensionList = DimensionService.getDimensionList();
        } catch (Exception e) {
            log.error("获取组织机构维度失败，无法通过路径解析组织机构...", e);
            throw e;
        }
        List<DimensionInfo> list = dimensionList.getList();
        if (!CollectionUtils.isEmpty(list)) {
            List<DimensionInfo> organizationDimension = list.stream().filter(k -> ORG_DIMENSION_TYPE.equals(k.getType())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(organizationDimension)) {
                DIMENSION_ID = organizationDimension.get(0).getId();
                return;
            }
        }
        throw new BusinessException("获取组织机构维度失败，无法通过路径解析组织机构...");
    }

    private static OrgDto buildOrgDto(OrgChildrenVO childrenVo) {
        List<OrgBaseDto> orgPathList = new ArrayList<>();
        OrgDto dto = new OrgDto();
        while (true) {
            OrgBaseDto orgPathDto = new OrgBaseDto(childrenVo.getId(), childrenVo.getName(), childrenVo.getCode());
            orgPathList.add(orgPathDto);
            // 最后一个子组织时,设置父级路径,结束循环，最后一个组织就是本身
            if (childrenVo.getChildren() == null) {
                dto.setOrgId(childrenVo.getId());
                dto.setOrgName(childrenVo.getName());
                dto.setOrgCode(childrenVo.getCode());
                break;
            }
            childrenVo = childrenVo.getChildren();
        }
        dto.setOrgPathList(orgPathList);
        return dto;
    }
}
