package cn.cisdigital.datakits.framework.web.mvc.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * 自定义转换器
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * @author xxx
 * @since 2024-04-22
 */
public interface CustomConverter<S, T> extends Converter<S, T> {

}
