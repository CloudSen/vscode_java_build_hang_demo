package cn.cisdigital.datakits.framework.web.mvc;

import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.cisdigital.datakits.framework.web.mvc.converter.CustomConverter;
import cn.cisdigital.datakits.framework.web.mvc.converter.CustomConverterFactory;
import cn.cisdigital.datakits.framework.web.mvc.interceptor.CustomInterceptor;
import cn.cisdigital.datakits.framework.web.mvc.serializer.ObjectMapperPostProcessor;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author xxx
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("NullableProblems")
@ConditionalOnProperty(name = WebConstants.ENABLE_MVC, havingValue = "true", matchIfMissing = true)
public class MvcAutoConfiguration implements WebMvcConfigurer {

    static {
        log.info(WebConstants.LOADING_MVC_AUTO_CONFIGURE);
    }

    private final List<CustomConverter> customConverters;
    private final List<CustomInterceptor> customInterceptors;
    private final List<CustomConverterFactory> customConverterFactories;
    private final List<ObjectMapperPostProcessor> objectMapperPostProcessors;

    /**
     * 使用优化后的object mapper
     */
    @Bean
    public MappingJackson2HttpMessageConverter cisdiMsgConverter() {
        return new MappingJackson2HttpMessageConverter(fastObjectMapper());
    }

    @Bean
    public ObjectMapper fastObjectMapper() {
        ObjectMapper objectMapper = JsonUtils.OBJECT_MAPPER;
        if (CollUtil.isEmpty(objectMapperPostProcessors)) {
            return objectMapper;
        }
        objectMapperPostProcessors.forEach(processor -> {
            processor.postConfig(objectMapper);
        });
        return objectMapper;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
        converters.add(0, cisdiMsgConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        if (CollUtil.isNotEmpty(customInterceptors)) {
            customInterceptors.forEach(customInterceptor -> registry.addInterceptor(customInterceptor)
                    .addPathPatterns(customInterceptor.pathPatterns())
                    .excludePathPatterns(customInterceptor.excludePathPatterns()));
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        if (CollUtil.isNotEmpty(customConverterFactories)) {
            customConverterFactories.forEach(registry::addConverterFactory);
        }
        if (CollUtil.isNotEmpty(customConverters)) {
            customConverters.forEach(registry::addConverter);
        }
    }
}
