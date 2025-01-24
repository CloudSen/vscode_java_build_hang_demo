package cn.cisdigital.datakits.framework.web.log;

import static cn.cisdigital.datakits.framework.web.constant.WebConstants.LOG_PARAMETER_SEPARATOR;

import cn.cisdigital.datakits.framework.web.log.annotation.LogIgnore;
import cn.cisdigital.datakits.framework.web.log.annotation.LogInvoke;
import java.lang.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


/**
 * 方法日志打印
 *
 * @author xxx
 */
@Slf4j
@Aspect
@Component
public class LogAop {

    @Around("execution(* cn.cisdigital..*.*(..)) && (@target(annotation) || @annotation(annotation)) && !@annotation(cn.cisdigital.datakits.framework.web.log.annotation.LogIgnore)")
    public Object logInvoke(ProceedingJoinPoint joinPoint, LogInvoke annotation) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String parameter = buildParameterInfo(joinPoint);
        log.info("before method invoke, class: {}, method: {}, parameter: {}", className, methodName, parameter);

        boolean isLogReturnValue = isLogReturnValue(joinPoint, annotation);
        long startTime = System.currentTimeMillis();
        try {
            Object returnValue = joinPoint.proceed();
            long invokeMillis = System.currentTimeMillis() - startTime;
            String finishLogTemplate = "invoke finish, class: {}, method: {}, invokeMillis: {}, parameter: {}";
            if (isLogReturnValue) {
                log.info(finishLogTemplate + ", return: {}", className, methodName, invokeMillis, parameter, returnValue);
            } else {
                log.info(finishLogTemplate, className, methodName, invokeMillis, parameter);
            }
            return returnValue;
        } catch (Exception e) {
            long invokeMillis = System.currentTimeMillis() - startTime;
            log.error("invoke exception, class: " + className + ", method: " + methodName + ", parameter: " + parameter + ", invokeMillis: " + invokeMillis, e);
            throw e;
        }
    }

    private boolean isLogReturnValue(ProceedingJoinPoint joinPoint, LogInvoke annotation) {
        Class<?> invokeClass = joinPoint.getTarget().getClass();
        LogInvoke classAnnotation = invokeClass.getAnnotation(LogInvoke.class);
        return annotation != null ? annotation.isLogReturnValue() : classAnnotation.isLogReturnValue();
    }

    private String buildParameterInfo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();

        StringBuilder parameterBuilder = new StringBuilder();
        for (int i = 0, length = args.length; i < length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            if (!hasIgnoreAnnotation(parameterAnnotation)) {
                parameterBuilder.append(args[i]).append(LOG_PARAMETER_SEPARATOR);
            }
        }

        if (parameterBuilder.length() > LOG_PARAMETER_SEPARATOR.length()) {
            parameterBuilder.deleteCharAt(parameterBuilder.length() - LOG_PARAMETER_SEPARATOR.length());
        }
        return parameterBuilder.toString();
    }

    private boolean hasIgnoreAnnotation(Annotation[] parameterAnnotation) {
        for (int i = parameterAnnotation.length - 1; i >= 0; i--) {
            Annotation annotation = parameterAnnotation[i];
            if (annotation instanceof LogIgnore) {
                return true;
            }
        }
        return false;
    }

}
