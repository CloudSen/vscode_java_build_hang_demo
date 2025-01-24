package cn.cisdigital.datakits.framework.dynamic.datasource.properties;

import com.alibaba.druid.sql.parser.Token;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author xxx
 * @since 2024-06-26
 */
@Component
@ConfigurationPropertiesBinding
public class SqlTokenCoverter implements Converter<String, Token> {

    @Override
    public Token convert(@NonNull String source) {
        return Token.valueOf(StringUtils.upperCase(source));
    }
}
