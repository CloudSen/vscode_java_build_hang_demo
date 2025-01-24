package cn.cisdigital.datakits.framework.cloud.alibaba.service;

import cn.cisdigital.datakits.framework.cloud.alibaba.exception.RemoteServiceErrorCode;
import cn.cisdigital.datakits.framework.cloud.alibaba.feign.RestTemplateWrapper;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RemoteCountProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RemoteServiceInfo;
import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author xxx
 * @since 2024-03-21
 */
@Service
public class RemoteCountService {

    private final RestTemplate restTemplate;
    private final RemoteCountProperties properties;

    public RemoteCountService(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate,
        RemoteCountProperties properties) {

        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * 统计某个类型的总和
     *
     * @param type 统计类型
     * @return 类型总和
     */
    public long count(String type, Object... params) {
        List<RemoteServiceInfo> allServices = checkServices();
        List<RemoteServiceInfo> targetServices = filterService(allServices, type);
        return targetServices.stream().mapToLong(service -> {
            Object data = RestTemplateWrapper.getData(
                () -> restTemplate.getForObject(service.getUrl(), ResVo.class, params));
            return Long.parseLong(data.toString());
        }).sum();
    }

    /**
     * 统计某批类型的总和
     *
     * @param types 多种统计类型
     * @return 类型总和
     */
    public long count(List<String> types, Object... params) {
        List<RemoteServiceInfo> allServices = checkServices();
        List<RemoteServiceInfo> targetServices = filterService(allServices, types);
        return targetServices.stream().mapToLong(service -> {
            Object data = RestTemplateWrapper.getData(
                () -> restTemplate.getForObject(service.getUrl(), ResVo.class, params));
            return Long.parseLong(data.toString());
        }).sum();
    }

    /**
     * 单独统计每个提供方某种类型的总和
     *
     * @param type 统计类型
     * @return key：提供方，value：总和
     */
    public Map<String, Long> countMapByProvider(String type, Object... params) {
        List<RemoteServiceInfo> allServices = checkServices();
        List<RemoteServiceInfo> targetServices = filterService(allServices, type);
        return targetServices.stream().collect(Collectors.toMap(
            RemoteServiceInfo::getProvider,
            service -> {
                Object data = RestTemplateWrapper.getData(
                    () -> restTemplate.getForObject(service.getUrl(), ResVo.class, params));
                return Long.parseLong(data.toString());
            }
        ));
    }

    /**
     * 单独统计每个提供方多种类型的总和
     *
     * @param types 多种统计类型
     * @return key：提供方，value：总和
     */
    public Map<String, Long> countMapByProvider(List<String> types, Object... params) {
        List<RemoteServiceInfo> allServices = checkServices();
        List<RemoteServiceInfo> targetServices = filterService(allServices, types);
        return targetServices.stream().collect(Collectors.toMap(
            RemoteServiceInfo::getProvider,
            service -> {
                Object data = RestTemplateWrapper.getData(
                    () -> restTemplate.getForObject(service.getUrl(), ResVo.class, params));
                return Long.parseLong(data.toString());
            }
        ));
    }

    /**
     * 单独统计每个提供方某种类型的详细信息
     *
     * @param type 统计类型
     * @return key：提供方，value：详细信息
     */
    public <T> Map<String, T> countMapByProviderV2(String type, Object... params) {
        List<RemoteServiceInfo> allServices = checkServices();
        List<RemoteServiceInfo> targetServices = filterService(allServices, type);
        return targetServices.stream().collect(Collectors.toMap(
            RemoteServiceInfo::getProvider,
            service -> {
                Object data = RestTemplateWrapper.getData(
                    () -> restTemplate.getForObject(service.getUrl(), ResVo.class, params));
                return (T) data;
            }
        ));
    }

    private List<RemoteServiceInfo> checkServices() {
        List<RemoteServiceInfo> services = properties.getServices();
        if (CollUtil.isEmpty(services)) {
            throw new BusinessException(RemoteServiceErrorCode.SERVICE_MISSING);
        }
        return services;
    }

    private List<RemoteServiceInfo> filterService(List<RemoteServiceInfo> services, String type) {
        if (CollUtil.isEmpty(services)) {
            throw new BusinessException(RemoteServiceErrorCode.SERVICE_MISSING);
        }
        if (CharSequenceUtil.isBlank(type)) {
            throw new BusinessException(RemoteServiceErrorCode.SERVICE_TYPE_MISSING);
        }
        return services.stream()
            .filter(remoteServiceInfo -> remoteServiceInfo.getType().equals(type))
            .collect(Collectors.toList());
    }

    private List<RemoteServiceInfo> filterService(List<RemoteServiceInfo> services, List<String> types) {
        if (CollUtil.isEmpty(services)) {
            throw new BusinessException(RemoteServiceErrorCode.SERVICE_MISSING);
        }
        if (CollUtil.isEmpty(types)) {
            throw new BusinessException(RemoteServiceErrorCode.SERVICE_TYPE_MISSING);
        }
        return services.stream()
            .filter(remoteServiceInfo -> types.contains(remoteServiceInfo.getType()))
            .collect(Collectors.toList());
    }
}
