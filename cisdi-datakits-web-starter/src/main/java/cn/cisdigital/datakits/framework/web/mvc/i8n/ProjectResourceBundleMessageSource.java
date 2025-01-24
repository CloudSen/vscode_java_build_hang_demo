package cn.cisdigital.datakits.framework.web.mvc.i8n;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 国际化资源加载策略
 *
 * <p>
 * 支持读取jar包及项目内的国际化资源加载器
 * </p>
 *
 * @author xxx
 */
@Slf4j
@Component("messageSource")
public class ProjectResourceBundleMessageSource extends ResourceBundleMessageSource {

    @Value(value = "${spring.messages.basename}")
    private String basename;

    @PostConstruct
    public void init() {
        log.info("[自动装配] 加载国际化资源加载器");

        if (StringUtils.hasText(basename)) {
            List<String> fileNames;
            if (basename.contains(",")) {
                fileNames = Arrays.stream(basename.split(",")).collect(Collectors.toList());
            } else {
                fileNames = Collections.singletonList(basename);
            }

            List<String> allBaseNames = new ArrayList<>();
            for (String baseFileName : fileNames) {
                String filePath = baseFileName.trim() + ".properties";
                URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePath);
                log.info("url.file: {}", filePath);
                if (null == fileUrl) {
                    throw new RuntimeException(
                        "[自动装配] 无法获取spring.messages.basename配置项指定的国际化资源, 请检查文件路径: " + filePath + " 是否正确, 处理后再尝试启动");
                }
                allBaseNames.add(baseFileName);
            }
            super.setBasenames(allBaseNames.toArray(new String[0]));
        }

        super.setBundleClassLoader(Thread.currentThread().getContextClassLoader());
        super.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }
}
