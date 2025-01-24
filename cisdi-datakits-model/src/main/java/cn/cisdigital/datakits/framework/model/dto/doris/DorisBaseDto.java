package cn.cisdigital.datakits.framework.model.dto.doris;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author xxx
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DorisBaseDto implements Serializable {

    /**
     * 命名空间
     */
    String nsName = "default_cluster";
    /**
     * host
     */
    @NonNull
    String host;
    /**
     * fe节点的端口
     */
    @NonNull
    Integer fePort;
    /**
     * 用户名
     */
    @NonNull
    String username;
    /**
     * 密码
     */
    @NonNull
    @ToString.Exclude
    String password;
}
