package cn.cisdigital.datakits.framework.web.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import cn.cisdigital.datakits.framework.web.WebAutoConfiguration;
import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * logback国际化配置
 *
 * @author xxx
 * @since 2024-03-06
 */
@Configuration
@AutoConfigureAfter(WebAutoConfiguration.class)
public class LogContextAutoConfiguration implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.addTurboFilter(new ModifyingFilter());
    }

    private static class ModifyingFilter
        extends TurboFilter {

        @Override
        public FilterReply decide(
            Marker marker,
            ch.qos.logback.classic.Logger logger,
            Level level,
            String format,
            Object[] params,
            Throwable t) {

            if (format == null) {
                return FilterReply.NEUTRAL;
            }
            try {
                String i18nMsg = I18nUtils.getOriginMessageIfNotExits(format);
                if (CharSequenceUtil.equals(i18nMsg, format)) {
                    return FilterReply.NEUTRAL;
                }
                String finalMessage = CharSequenceUtil.join(" | ", format,
                    I18nUtils.getOriginMessageIfNotExits(format, params));
                logger.log(marker, logger.getName(), Level.toLocationAwareLoggerInteger(level), finalMessage, null, t);
                return FilterReply.DENY;
            } catch (Exception e) {
                return FilterReply.NEUTRAL;
            }
        }
    }
}
