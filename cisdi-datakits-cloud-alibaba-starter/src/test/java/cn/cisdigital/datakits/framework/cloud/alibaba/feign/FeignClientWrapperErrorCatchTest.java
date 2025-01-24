package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.cloud.alibaba.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import feign.FeignException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class FeignClientWrapperErrorCatchTest extends BaseIntegrationTest {

    @Mock
    private Supplier<ResVo<String>> mockSupplier;

    @Test
    void testGetData_Success() {
        ResVo<String> successResponse = ResVo.ok("测试数据");
        when(mockSupplier.get()).thenReturn(successResponse);

        Optional<String> result = FeignClientWrapper.getData(mockSupplier);

        assertTrue(result.isPresent());
        assertEquals("测试数据", result.get());
    }

    @Test
    void testGetData_UnknownHostException() {
        when(mockSupplier.get()).thenThrow(new RuntimeException(new UnknownHostException("未知主机")));

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals(FeignErrorCode.FEIGN_UNKNOWN_HOST, exception.getErrorCode());
    }

    @Test
    void testGetData_SocketTimeoutException() {
        when(mockSupplier.get()).thenThrow(new RuntimeException(new SocketTimeoutException("连接超时")));

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals(FeignErrorCode.FEIGN_CALL_TIMEOUT, exception.getErrorCode());
    }

    @Test
    void testGetData_FeignNotFound() {
        when(mockSupplier.get()).thenThrow(FeignException.NotFound.class);

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals(FeignErrorCode.FEIGN_CALL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetData_OtherException() {
        when(mockSupplier.get()).thenThrow(new RuntimeException("其他异常"));

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals(FeignErrorCode.FEIGN_CALL_EXCEPTION, exception.getErrorCode());
    }

    @Test
    void testGetData_NullResult() {
        when(mockSupplier.get()).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals(FeignErrorCode.FEIGN_NULL_RESULT, exception.getErrorCode());
    }

    @Test
    void testGetData_FailureResponse() {
        ResVo<String> failureResponse = ResVo.error("ERROR_CODE", "失败消息");
        when(mockSupplier.get()).thenReturn(failureResponse);

        BusinessException exception = assertThrows(BusinessException.class, () -> FeignClientWrapper.getData(mockSupplier));
        assertEquals("ERROR_CODE", exception.getErrorCode().getCode());
        assertEquals("失败消息", exception.getMessage());
    }

    // 可以为其他方法添加类似的测试用例

}
