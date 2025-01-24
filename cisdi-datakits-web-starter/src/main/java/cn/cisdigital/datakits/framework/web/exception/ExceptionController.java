package cn.cisdigital.datakits.framework.web.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理Filter层的异常
 *
 * @author xxx
 * @since 202Rest-07
 */
@RestController
public class ExceptionController {

    @RequestMapping("/filter/error")
    public void handleException(HttpServletRequest request) {
        Object filterError = request.getAttribute("filterError");
        throw new BusinessException(filterError.toString());
    }
}
