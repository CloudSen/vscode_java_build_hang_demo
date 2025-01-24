package cn.cisdigital.datakits.framework.web.transaction;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 资源类型
 *
 * @author xxx
 * @since 2023-05-17
 */
@Getter
@RequiredArgsConstructor
public enum ResourceScheme implements BaseEnum {

    /**
     * 2 minio
     */
    MINIO(2, "minio")
    ;

    @EnumValue
    private final int code;
    private final String key;

    public static ResourceScheme of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
            .findAny().orElse(null);
    }
}
