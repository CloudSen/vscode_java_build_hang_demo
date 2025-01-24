package cn.cisdigital.datakits.framework.web.threadpool;

import cn.cisdigital.datakits.framework.web.WebConfigProperties;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步方法线程池
 *
 * @author xxx
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = WebConstants.ENABLE_ASYNC_THREAD_POOL, havingValue = "true")
public class AsyncTaskThreadPoolAutoConfiguration {

    static {
        log.info(WebConstants.LOADING_ASYNC_THREAD_POOL);
    }

    private final WebConfigProperties webConfigProperties;

    @Bean
    @ConditionalOnMissingBean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(webConfigProperties.getAsyncPool().getCorePoolSize());
        executor.setMaxPoolSize(webConfigProperties.getAsyncPool().getMaxPoolSize());
        executor.setQueueCapacity(webConfigProperties.getAsyncPool().getQueueCapacity());
        executor.setThreadNamePrefix(webConfigProperties.getAsyncPool().getThreadNamePrefix());
        executor.setAllowCoreThreadTimeOut(webConfigProperties.getAsyncPool().getAllowCoreThreadTimeout());
        executor.setKeepAliveSeconds((int) webConfigProperties.getAsyncPool().getKeepAliveSeconds().getSeconds());
        executor.setTaskDecorator(new AsyncTaskDecorator());
        executor.initialize();
        return executor;
    }
}
