package cn.cisdigital.datakits.framework.web.mvc.converter;

import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 自定义类型转换工厂
 *
 * @param <S> 源类型
 * @param <R> 目标类型
 * @author xxx
 * @since @since 2024-04-22
 */
public interface CustomConverterFactory<S, R> extends ConverterFactory<S, R> {

}
