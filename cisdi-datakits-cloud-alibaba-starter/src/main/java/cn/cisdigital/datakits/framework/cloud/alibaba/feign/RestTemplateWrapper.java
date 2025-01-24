package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.function.Supplier;

/**
 * @author xxx
 * @since 2024-03-21
 */
public final class RestTemplateWrapper {

    public static Object getData(Supplier<ResVo> supplier) {
        ResVo resVo;
        try {
            resVo = supplier.get();
        } catch (Exception e) {
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_EXCEPTION, e);
        }
        if (resVo == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_NULL_RESULT);
        }
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            return resVo.getData();
        }
        throw new BusinessException(resVo.getMessage());
    }
}
