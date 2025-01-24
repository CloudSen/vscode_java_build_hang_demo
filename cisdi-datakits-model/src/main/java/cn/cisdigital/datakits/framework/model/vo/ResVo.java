package cn.cisdigital.datakits.framework.model.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

/**
 * 统一响应JSON对象
 *
 * @author xxx
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResVo<T> implements Serializable {

    private static final long serialVersionUID = -709109828798783625L;

    /**
     * 错误代码 * @default 0 * @mock @pick([0, 500])
     */
    private String code;

    /**
     * 消息
     *
     * @mock 操作提示
     */
    private String message;

    /**
     * 响应数据
     */
    private T data; // NOSONAR

    private String requestId;

    public static final String OPT_SUCCESS = "success";
    public static final String OPT_ERROR = "error";
    public static final String ERROR_CODE = "-1";
    public static final String SUCCESS_CODE = "0";

    public static <T> ResVo<T> ok() {
        return ok(null, null);
    }

    public static <T> ResVo<T> ok(T data) {
        return ok(OPT_SUCCESS, data);
    }

    public static <T> ResVo<T> ok(String msg, T data) {
        return new ResVo<T>().setMessage(msg).setData(data).setCode(SUCCESS_CODE);
    }

    public static <T> ResVo<T> error() {
        return new ResVo<T>().setMessage(OPT_ERROR).setRequestId(MDC.get("request-id"));
    }

    public static <T> ResVo<T> error(String msg) {
        return error(ERROR_CODE, msg);
    }

    public static <T> ResVo<T> error(T data) {
        return error(ERROR_CODE, null, data);
    }

    public static <T> ResVo<T> error(String code, String msg) {
        return error(code, msg, null);
    }

    public static <T> ResVo<T> error(String msg, T data) {
        return error(ERROR_CODE, msg, data);
    }

    public static <T> ResVo<T> error(String code, String msg, T data) {
        return new ResVo<T>().setCode(code).setMessage(msg).setData(data).setRequestId(MDC.get("request-id"));
    }

}
