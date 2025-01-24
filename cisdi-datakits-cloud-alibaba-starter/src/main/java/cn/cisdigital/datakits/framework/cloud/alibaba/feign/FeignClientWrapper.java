package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import static cn.hutool.core.text.CharPool.COLON;
import cn.hutool.core.text.CharSequenceUtil;
import static cn.hutool.core.text.StrPool.COMMA;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author xxx
 * @since 2024-03-19
 */
@Slf4j
public final class FeignClientWrapper {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REMOTE_RESULT = "远程调用结果：{}";

    private static void handleFeignException(Exception e) {
        if (e == null) {
            log.error("未知异常: 异常对象为null");
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_EXCEPTION);
        } else if (e instanceof RuntimeException && e.getCause() instanceof UnknownHostException) {
            log.error("未知服务名异常: {}", e.getCause().getMessage());
            throw new BusinessException(FeignErrorCode.FEIGN_UNKNOWN_HOST, e.getCause());
        } else if (e instanceof RuntimeException && e.getCause() instanceof SocketTimeoutException) {
            log.error("请求超时异常: {}", e.getCause().getMessage());
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_TIMEOUT, e.getCause());
        } else if (e instanceof FeignException.NotFound) {
            log.error("服务器404异常: {}", e.getMessage());
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_NOT_FOUND, e);
        } else {
            log.error("Feign调用异常: {}", e.getMessage());
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_EXCEPTION, e);
        }
    }

    public static void callWithoutData(Supplier<ResVo> supplier) {
        ResVo resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (!CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
        }
    }

    public static <T> Optional<T> getData(Supplier<ResVo<T>> supplier) {
        ResVo<T> resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            return Optional.ofNullable(resVo.getData());
        }
        throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
    }

    public static <T> Optional<T> getDataWithRequestIdMessage(Supplier<ResVo<T>> supplier) {
        ResVo<T> resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            return Optional.ofNullable(resVo.getData());
        }
        // 将 requestId 放入报错信息中
        String errorMessage = resVo.getMessage() + COMMA + REQUEST_ID_KEY + COLON + resVo.getRequestId();
        throw new BusinessException(new TempErrorCode(resVo.getCode(), errorMessage));
    }


    /**
     * 调用如果超时，抛出超时异常 FEIGN_CALL_TIMEOUT
     */
    public static void getTimeoutWithOutData(Supplier<ResVo> supplier) {
        ResVo resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (!CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
        }
    }


    public static <T> T getNonNullData(Supplier<ResVo<T>> supplier) {
        ResVo<T> resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            T data = resVo.getData();
            if (data == null) {
                throw new BusinessException(FeignErrorCode.FEIGN_DATA_IS_NULL);
            }
            return data;
        }
        throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
    }

    public static void checkVoidResult(Supplier<ResVo<Void>> supplier) {
        ResVo<Void> resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            return;
        }
        throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
    }

    public static void checkObjectResult(Supplier<ResVo<Object>> supplier) {
        ResVo<Object> resVo = null;
        try {
            resVo = supplier.get();
            log.info(REMOTE_RESULT, resVo);
        } catch (Exception e) {
            handleFeignException(e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            return;
        }
        throw new BusinessException(new TempErrorCode(resVo.getCode(), resVo.getMessage()));
    }

    @Data
    @AllArgsConstructor
    private static class TempErrorCode implements ErrorCode {

        private String code;
        private String key;
    }
}
