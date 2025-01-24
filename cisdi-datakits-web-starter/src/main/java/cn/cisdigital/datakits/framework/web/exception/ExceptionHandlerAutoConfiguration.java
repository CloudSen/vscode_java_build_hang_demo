package cn.cisdigital.datakits.framework.web.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.exception.CommonErrorCode;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理自动装配
 *
 * @author xxx
 */
@Slf4j
@RestController
@ControllerAdvice
@ConditionalOnProperty(name = WebConstants.ENABLE_EXCEPTION_HANDLER, havingValue = "true", matchIfMissing = true)
public class ExceptionHandlerAutoConfiguration {

    static {
        log.info(WebConstants.LOADING_EXCEPTION_HANDLER_AUTO_CONFIGURE);
    }

    public static final String BIZ_EXCEPTION = "common.exception.biz_error";
    public static final String METHOD_ARGUMENT_NOT_VALID = "common.exception.method_param_error";
    private static final String ERROR_FIELD_START_MARK = "[";
    private static final String ERROR_FIELD_END_MARK = "]";
    private static final String ERROR_FIELD_MESSAGE_END_MARK = "; ";


    /**
     * 方法参数错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> validationExceptionHandler(MethodArgumentNotValidException e) {
        log.error(METHOD_ARGUMENT_NOT_VALID, e);
        return handleMethodArgumentNotValidException(e);
    }

    /**
     * 方法参数错误
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> bindExceptionHandler(BindException e) {
        log.error(METHOD_ARGUMENT_NOT_VALID, e);
        return handleMethodArgumentNotValidException(e);
    }

    /**
     * 方法参数错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error(METHOD_ARGUMENT_NOT_VALID, e);
        return ResVo.error(e.getMessage());
    }

    /**
     * 请求参数错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error(METHOD_ARGUMENT_NOT_VALID, e);
        return ResVo.error(e.getMessage());
    }

    /**
     * JSR 303 Bean Validation校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> constrainViolationExceptionHandler(ConstraintViolationException e) {
        log.error(METHOD_ARGUMENT_NOT_VALID, e);
        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .map(I18nUtils::getOriginMessageIfNotExits)
            .collect(Collectors.joining());
        return ResVo.error(CommonErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> businessExceptionHandler(BusinessException e) {
        log.error(BIZ_EXCEPTION, e);
        ErrorCode commonErrorCode = Optional.ofNullable(e.getErrorCode()).orElse(CommonErrorCode.INTERNAL_ERROR);
        return ResVo.error(commonErrorCode.getCode(), e.getMessage());
    }

    /**
     * i18n异常，国际化文件KEY不存在
     */
    @ExceptionHandler(NoSuchMessageException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> noSuchMessageExceptionHandler(NoSuchMessageException e) {
        log.error(CommonErrorCode.INTERNAL_ERROR.getMsg(), e);
        return ResVo.error(CommonErrorCode.INTERNAL_ERROR.getMsg());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResVo<Object> otherExceptionHandler(Exception e) {
        log.error(CommonErrorCode.INTERNAL_ERROR.getMsg(), e);
        return ResVo.error(CommonErrorCode.INTERNAL_ERROR.getMsg());
    }

    //<editor-fold desc="读取参数校验相关异常信息">
    private ResVo<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResVo.error(buildFieldErrorMsg(e.getBindingResult()));
    }

    private ResVo<Object> handleMethodArgumentNotValidException(BindException e) {
        return ResVo.error(buildFieldErrorMsg(e.getBindingResult()));
    }

    private String buildFieldErrorMsg(BindingResult bindingResult) {
        List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
        if (CollectionUtils.isEmpty(fieldErrorList)) {
            return I18nUtils.getMessage(METHOD_ARGUMENT_NOT_VALID);
        }
        return fieldErrorList.stream()
            .map(error -> error == null || CharSequenceUtil.isBlank(error.getDefaultMessage()) ?
                I18nUtils.getMessage(METHOD_ARGUMENT_NOT_VALID)
                : buildFieldErrorMsg(error.getField(), I18nUtils.getOriginMessageIfNotExits(error.getDefaultMessage())))
            .collect(Collectors.joining());
    }

    private String buildFieldErrorMsg(String field, String errorMessage) {
        return ERROR_FIELD_START_MARK + field + ERROR_FIELD_END_MARK + errorMessage + ERROR_FIELD_MESSAGE_END_MARK;
    }
    //</editor-fold>


}
