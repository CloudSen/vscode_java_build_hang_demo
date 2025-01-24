package cn.cisdigital.datakits.framework.model.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Locale;

/**
 * 国际化工具类
 *
 * @author xxx
 * @since 2024-03-04
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class I18nUtils {

    private static final String MESSAGE_SOURCE_NAME = "messageSource";

    /**
     * 自动识别语言环境后，根据key获取对应的内容信息
     *
     * @param key 在国际化资源文件中对应的key
     * @throws org.springframework.context.NoSuchMessageException 如果key不存在，抛出该异常
     * @return 对应的内容信息
     */
    public static String getMessage(@Nonnull String key) {
        MessageSource messageSource = (MessageSource) SpringContext.getBean(MESSAGE_SOURCE_NAME);
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * 自动识别语言环境后，根据key获取对应的内容信息
     *
     * @param key 在国际化资源文件中对应的key
     * @return 对应的内容信息，如果key不存在，返回原始key
     */
    public static String getOriginMessageIfNotExits(@Nonnull String key) {
        try {
            MessageSource messageSource = (MessageSource) SpringContext.getBean(MESSAGE_SOURCE_NAME);
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(key, null, key, locale);
        } catch (Exception e) {
            log.error("获取国际化内容失败", e);
        }
        return key;
    }

    /**
     * 自动识别语言环境后，根据key获取对应的内容信息
     *
     * @param key  在国际化资源文件中对应的key
     * @param args 模板参数
     * @return 对应的内容信息，如果key不存在，返回原始key
     */
    public static String getOriginMessageIfNotExits(@Nonnull String key, @Nullable Object[] args) {
        try {
            MessageSource messageSource = (MessageSource) SpringContext.getBean(MESSAGE_SOURCE_NAME);
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(key, args, key, locale);
        } catch (Exception e) {
            log.error("获取国际化内容失败", e);
        }
        return key;
    }

    /**
     * 自动识别语言环境后，根据key获取对应的内容信息
     *
     * @param key        在国际化资源文件中对应的key
     * @param defaultMsg 国际化不存在时，使用的默认信息
     * @return 对应的内容信息
     */
    public static String getMessageWithDefault(@Nonnull String key, @Nonnull String defaultMsg) {
        try {
            MessageSource messageSource = (MessageSource) SpringContext.getBean(MESSAGE_SOURCE_NAME);
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(key, null, defaultMsg, locale);
        } catch (Exception e) {
            log.error("获取国际化内容失败", e);
        }
        return defaultMsg;
    }

    /**
     * 自动识别语言环境后，根据key和参数获取对应的内容信息
     *
     * @param key  在国际化资源文件中对应的key
     * @param args 模板参数
     * @throws org.springframework.context.NoSuchMessageException 如果key不存在，抛出该异常
     * @return 对应的内容信息
     */
    public static String getMessage(@Nonnull String key, @Nullable Object[] args) {
        MessageSource messageSource = (MessageSource) SpringContext.getBean(MESSAGE_SOURCE_NAME);
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}
