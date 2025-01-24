package cn.cisdigital.datakits.framework.web.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志打印标识
 * <p>
 * 打印入参、请求时长、返回结果、异常信息等日志
 * </p>
 * <p>
 * 可用于类和方法
 * </p>
 *
 * @author xxx
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogInvoke {

    /**
     * 是否打印返回值，非必要不打印
     */
    boolean isLogReturnValue() default false;

}
