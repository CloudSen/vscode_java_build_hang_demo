package cn.cisdigital.datakits.framework.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 应用状态
 *
 * @author xxx
 * @since 2022-07-28
 */
@Getter
@RequiredArgsConstructor
public enum AppState {

    ON(1, "开启"),

    OFF(0, "关闭"),

    ;

    /**
     * 代码
     */
    @EnumValue
    @JsonValue
    private final int code;
    /**
     * desc
     */
    private final String desc;

    public static AppState of(int code) {
        for (AppState type : AppState.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
