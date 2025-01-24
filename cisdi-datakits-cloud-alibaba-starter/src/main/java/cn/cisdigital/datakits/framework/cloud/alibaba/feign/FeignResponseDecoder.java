package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign请求结果统一处理，返回data对象
 * <p>请勿使用</p>
 *
 * @deprecated 如果制定规范时，内部调用接口的定义可以不要ResVo，则启用此配置
 * @author xxx
 * @since 2024-03-19
 */
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class FeignResponseDecoder implements Decoder {

    private final ObjectMapper objectMapper;

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        Reader reader = response.body().asReader(StandardCharsets.UTF_8);
        ResVo<?> result = objectMapper.readValue(reader, ResVo.class);
        if (result == null) {
            throw new BusinessException(FeignErrorCode.FEIGN_CALL_EXCEPTION);
        }
        log.info("cloud-alibaba.log.feign_client_result", objectMapper.writeValueAsString(result));
        // 根据 code 判断操作是否成功
        if (CharSequenceUtil.equals(ResVo.SUCCESS_CODE, result.getCode())) {
            Object data = result.getData();
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return objectMapper.convertValue(data, javaType);
        }
        // 若不成功，抛出业务异常
        throw new BusinessException(result.getMessage());
    }
}
