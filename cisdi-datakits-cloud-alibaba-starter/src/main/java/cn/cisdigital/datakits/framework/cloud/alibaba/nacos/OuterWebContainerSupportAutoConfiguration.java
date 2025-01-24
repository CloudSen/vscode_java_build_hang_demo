package cn.cisdigital.datakits.framework.cloud.alibaba.nacos;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 让外部tomcat中的程序也能向nacos注册
 *
 * @author xxx
 * @implNote server.container-type未配置时，配置不会生效；配置1为东方通；配置2为宝兰德
 */
@Slf4j
@Configuration
@RefreshScope
@RequiredArgsConstructor
@ConditionalOnProperty("spring.cloud.nacos.discovery.enabled")
public class OuterWebContainerSupportAutoConfiguration implements ApplicationRunner {

    @Value("${server.port}")
    private Integer port;

    @Value("${server.container-type:-1}")
    private int containerType;

    private static final int DONG_FANG_TONG_CONTAINER_TYPE = 1;
    private static final int BAO_LAN_DE_CONTAINER_TYPE = 2;

    private final NacosAutoServiceRegistration registration;

    @Override
    public void run(ApplicationArguments args) {
        if (registration != null && port != null && containerType != -1) {
            Integer tomcatPort = port;
            try {
                Optional<Integer> optional = getTomcatPort();
                if (optional.isPresent()) {
                    tomcatPort = optional.get();
                    log.info("项目端口: {}", tomcatPort);
                } else {
                    log.error("从容器中获取到项目端口为空, 使用默认端口: " + port);
                }
            } catch (Exception e) {
                log.error("获取外部Tomcat端口出现错误", e);
            }
            registration.setPort(tomcatPort);
            registration.start();
        }
    }

    /**
     * 获取外部tomcat端⼝
     */
    public Optional<Integer> getTomcatPort() throws Exception {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames;
        switch (containerType) {
            case DONG_FANG_TONG_CONTAINER_TYPE:
                // 东方通容器获取端口方式
                log.info("获取东方通部署容器的端口");
                objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
                break;
            case BAO_LAN_DE_CONTAINER_TYPE:
                // 宝兰德容器获取端口方式
                log.info("获取宝兰德部署容器的端口");
                objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("name"), Query.value("http-listener-1")));
                break;
            default:
                return Optional.empty();
        }
        Iterator<ObjectName> iterator = Optional.ofNullable(objectNames).orElse(new HashSet<>()).stream()
            .filter(Objects::nonNull).iterator();
        if (iterator.hasNext()) {
            String tomcatPort = iterator.next().getKeyProperty("port");
            return StringUtils.hasText(tomcatPort) ? Optional.of(Integer.valueOf(tomcatPort)) : Optional.empty();
        }
        log.info("未获取到外部WEB容器端口");
        return Optional.empty();
    }
}
