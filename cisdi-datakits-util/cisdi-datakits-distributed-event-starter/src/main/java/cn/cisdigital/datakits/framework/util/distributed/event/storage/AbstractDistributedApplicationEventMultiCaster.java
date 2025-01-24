package cn.cisdigital.datakits.framework.util.distributed.event.storage;

import cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xxx
 * @since 2024/6/25 14:08
 */
@Service
@Slf4j
public abstract class AbstractDistributedApplicationEventMultiCaster
        extends SimpleApplicationEventMulticaster implements DistributedEventStorage, ApplicationContextAware {

    protected final Set<String> existsEventClassNameSet = new HashSet<>();


    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        log.info("scan DistributedEvent class begin");

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(DistributedEvent.class));
        // scan in cn.cisdigital
        final Set<BeanDefinition> candidateComponents = provider.findCandidateComponents("cn.cisdigital");
        for (BeanDefinition component : candidateComponents) {
            existsEventClassNameSet.add(component.getBeanClassName());
        }
        log.info("scan DistributedEvent class end size: {}, detail : {}", existsEventClassNameSet.size(),existsEventClassNameSet);
    }

    /**
     * 检查当前jvm中是否存在事件的实例类
     * @param className 全路径的类名称，eg:cn.xx.xx.xxEvent
     * @return true 存在
     */
    protected boolean checkEventExists(String className){
        return existsEventClassNameSet.contains(className);
    }
}
