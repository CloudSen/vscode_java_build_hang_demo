package cn.cisdigital.datakits.framework.util.qbeeopensdk.auto.convertor;

import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDetailDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.EmployeeDto;
import cn.cisdigital.datakits.framework.model.dto.qbee.open.OrgDto;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.service.EmployeeCacheService;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.service.OrgCacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024/12/11 15:25
 */
@Slf4j
public final class AutoConvertorService {
    private AutoConvertorService() {
    }

    /**
     * 批量设置组织机构名
     *
     * @param convertorList      业务对象
     * @param getOrgIdFunction   获取组织机构id方法
     * @param setOrgNameFunction 回传设置组织机构名称方法
     * @param <T>                业务类型
     */
    public static  <T> void convertOrgName(List<T> convertorList, Function<T, String> getOrgIdFunction, FillFunction<T, String> setOrgNameFunction) {
        convertCommon(convertorList, getOrgIdFunction, setOrgNameFunction, OrgCacheService::getOrgBatch, OrgDto::getOrgName);
    }

    /**
     * 通用转换员工用户名, 将employeeId 转为 employeeName
     *
     * @param convertorList         业务对象List
     * @param getEmployIdFunction   getEmployIdFunction,提供从业务对象获取employeeId
     * @param setEmployNameFunction setEmployNameFunction,提供回填employeeName到业务对象
     * @param <T>                   业务对象类型
     */
    public static <T> void convertEmployeeName(List<T> convertorList, Function<T, String> getEmployIdFunction, FillFunction<T, String> setEmployNameFunction) {
        convertCommon(convertorList, getEmployIdFunction, setEmployNameFunction, EmployeeCacheService::getEmployeeBatch, EmployeeDto::getEmployeeName);
    }

    /**
     * 通用转换员工明细信息
     *
     * @param convertorList       业务对象List
     * @param getEmployIdFunction 提供从业务对象获取employeeId
     * @param setDetailFunction   提供回填detail到业务对象
     * @param <T>                 业务对象类型
     */
    public static <T> void convertEmployeeDetail(List<T> convertorList, Function<T, String> getEmployIdFunction, FillFunction<T, EmployeeDetailDto> setDetailFunction) {
        convertCommon(convertorList, getEmployIdFunction, setDetailFunction, EmployeeCacheService::getEmployeeDetailBatch, detail -> detail);
    }


    /**
     * 通用填充员工明细信息方法
     *
     * @param convertorList       待转换列表
     * @param getKeyWordFunction  获取key方法
     * @param setDetailFunction   设置明细方法
     * @param getCacheMapFunction 获取缓存方法
     * @param <T>                 业务对象类型
     * @param <R>                 回传类型
     * @param <C>                 缓存类型
     */
    public static <T, R, C> void convertCommon(List<T> convertorList,
                                        Function<T, String> getKeyWordFunction,
                                        FillFunction<T, R> setDetailFunction,
                                        Function<Collection<String>, Map<String, Optional<C>>> getCacheMapFunction,
                                        Function<C, R> convertToConsumerParamFunction) {
        if (CollectionUtils.isEmpty(convertorList)) {
            return;
        }
        final Collection<String> keyWordIds = getKeyWordIds(convertorList, getKeyWordFunction);
        try {
            final Map<String, Optional<C>> cacheMap = getCacheMapFunction.apply(keyWordIds);

            convertorList.forEach(businessDto -> {
                String key = getKeyWordFunction.apply(businessDto);
                if (cacheMap.containsKey(key)) {
                    Optional<C> detailInfoOptional = cacheMap.get(key);
                    detailInfoOptional.ifPresent(detail -> setDetailFunction.setParam(businessDto, convertToConsumerParamFunction.apply(detail)));
                }
            });
        } catch (Exception e) {
            log.warn("convertAbstract error:{}", e.getMessage());
        }
    }

    /**
     * 批量获取缓存key
     *
     * @param convertorList      带转换业务对象列表
     * @param getKeyWordFunction 提供从业务对象获取keyWordId
     * @param <T>                业务对象类型
     * @return 缓存key集合
     */
    private static <T> Collection<String> getKeyWordIds(Collection<T> convertorList, Function<T, String> getKeyWordFunction) {
        return convertorList.stream()
                .filter(r -> StringUtils.isNotBlank(getKeyWordFunction.apply(r)))
                .map(getKeyWordFunction)
                .collect(Collectors.toSet());
    }

}
