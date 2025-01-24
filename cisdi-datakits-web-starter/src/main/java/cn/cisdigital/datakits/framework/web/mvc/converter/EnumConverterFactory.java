package cn.cisdigital.datakits.framework.web.mvc.converter;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 枚举数字转枚举
 *
 * <p>适用于ConversionService，处理@RequestParam 和 @PathVariable的参数</p>
 *
 * @author xxx
 * @since 2024-04-22
 */
@Component
public class EnumConverterFactory implements CustomConverterFactory<String, BaseEnum> {

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new IntegerToEnum<>(targetType);
    }

    private static class IntegerToEnum<T extends BaseEnum> implements Converter<String, T> {

        private final T[] values;

        public IntegerToEnum(Class<T> targetType) {
            values = targetType.getEnumConstants();
        }

        @Override
        public T convert(String source) {
            for (T t : values) {
                if (CharSequenceUtil.isBlank(source)) {
                    return null;
                }
                if (t.getCode() == Integer.parseInt(source)) {
                    return t;
                }
            }
            return null;
        }
    }
}
