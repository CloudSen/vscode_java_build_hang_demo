package cn.cisdigital.datakits.framework.model.dto.database;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString.Exclude;

/**
 * @author xxx
 * @since 2022-08-19-10:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class MqSourceDto {

    /**
     * RocketMQ url 地址
     */
    @NonNull
    private String url;
    /**
     * RocketMQ 用户名
     */
    private String username;
    /**
     * RocketMQ 密码
     */
    @Exclude
    private String password;
    /**
     * 生产者组名称
     */
    private String producerGroup;
    /**
     * 消费者者组名称
     */
    private String consumerGroup;

    public MqSourceDto(String url, String producerGroup, String consumerGroup) {
        this.url = url;
        this.producerGroup = producerGroup;
        this.consumerGroup = consumerGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MqSourceDto that = (MqSourceDto) o;
        return Objects.equal(url, that.url) && Objects.equal(username, that.username)
            && Objects.equal(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, username, password);
    }
}
