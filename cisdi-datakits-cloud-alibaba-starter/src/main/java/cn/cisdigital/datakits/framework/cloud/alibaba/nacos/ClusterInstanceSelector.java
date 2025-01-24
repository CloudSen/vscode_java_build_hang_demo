package cn.cisdigital.datakits.framework.cloud.alibaba.nacos;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.ShardDto;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集群实例内分片工具类
 *
 * @author xxx
 * @since 2024/5/6
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnNacosDiscoveryEnabled
public class ClusterInstanceSelector {

    public static final int NOT_FOUND_INDEX = -1;
    private final DiscoveryClient discoveryClient;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;


    /**
     * 获取当前实例分片信息
     *
     * @return 分片结果, 如果未获取到Nacos实例, 则定义为独立的分片, 分片总数为1
     */
    public ShardDto getCurrentShard() {
        List<ServiceInstance> instances = discoveryClient.getInstances(nacosDiscoveryProperties.getService());
        int currentInstanceIndex = getCurrentInstanceIndex(instances);
        if (currentInstanceIndex == NOT_FOUND_INDEX) {
            return new ShardDto(0, 1);
        }
        return new ShardDto(currentInstanceIndex, instances.size());
    }

    /**
     * 获取当前实例分片(仅适用于小量数据)
     *
     * @param ids 待分片ID集合
     * @return 分片结果
     */
    public Set<Long> getCurrentSlice(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptySet();
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(nacosDiscoveryProperties.getService());
        int currentInstanceIndex = getCurrentInstanceIndex(instances);
        if (currentInstanceIndex < 0) {
            return Collections.emptySet();
        } else {
            return ids.stream().filter(id -> select(id, instances.size(), currentInstanceIndex))
                    .collect(Collectors.toSet());
        }
    }

    /**
     * 获取当前实例的索引位置: 根据实例IP+PORT排序
     *
     * @param allInstances 当前服务的全部实例
     * @return 排序后当前实例所在数组下标
     */
    private int getCurrentInstanceIndex(List<ServiceInstance> allInstances) {
        if (CollUtil.isEmpty(allInstances)) {
            return NOT_FOUND_INDEX;
        }
        Predicate<ServiceInstance> matchedService =
                instance -> instance.getHost().equals(nacosDiscoveryProperties.getIp())
                        && instance.getPort() == nacosDiscoveryProperties.getPort();
        return allInstances.stream()
                .filter(matchedService)
                .findFirst()
                .map(currentInstance -> {
                    //按照 IP+PORT 排序, 取当前实例的下标
                    List<String> instanceIdList = allInstances.stream()
                            .map(serviceInstance -> serviceInstance.getHost() + serviceInstance.getPort())
                            .sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                    return instanceIdList.indexOf(currentInstance.getHost() + currentInstance.getPort());
                })
                .orElse(NOT_FOUND_INDEX);
    }

    /**
     * 分片函数 可由子类重写, 默认分片规则 id % total == currentInstanceIndex
     *
     * @param id                   待分片ID
     * @param total                总分片数
     * @param currentInstanceIndex 当前实例索引位置
     * @return 对象是否被分到当前实例
     */
    public boolean select(Long id, int total, int currentInstanceIndex) {
        return id % total == currentInstanceIndex;
    }

    /**
     * 按时间维度分片 判断该实例是否执行任务
     * @param timeType 仅仅支持MINUTES HOURS DAYS
     * @return
     */
    public boolean executeInstanceByTimeSlice(ChronoUnit timeType){
        int input = 0;
        switch (timeType){
            case MINUTES:
                input = LocalDateTime.now().getMinute();
                break;
            case HOURS:
                input = LocalDateTime.now().getHour();
                break;
            case DAYS:
                input = LocalDateTime.now().getDayOfMonth();
                break;
            default:
                throw new BusinessException("仅仅支持按分、小时、天的维度做实例分片");
        }
        ShardDto currentShard = this.getCurrentShard();
        int shardIndex = currentShard.getShardIndex();
        int shardTotal = currentShard.getShardTotal();
        return input % shardTotal == shardIndex;
    }
}
