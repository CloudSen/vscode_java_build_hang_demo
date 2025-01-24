package cn.cisdigital.datakits.framework.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * 业务使用到的redis key管理
 *
 * @author xxx
 * @since 2024-12-05
 */
@Validated
@Data
@ConfigurationProperties(prefix = "public.redis.business-keys")
public class RedisKeyProperties implements Serializable{

    /**
     * cdc能提交到doris的事物总数
     */
    private String cdcDorisTxSemaphoreKey;

    /**
     * etl能提交到doris的事物总数
     */
    private String etlDorisTxSemaphoreKey;

    /**
     * collie能提交到doris的事物总数
     */
    private String collieDorisTxSemaphoreKey;

    /**
     * doris是否可用的标志
     */
    private String dorisAvailableKey;

    public RedisKeyProperties() {
        this.cdcDorisTxSemaphoreKey = "datakits:resource:cdc_doris_tx_semaphore";
        this.etlDorisTxSemaphoreKey = "datakits:resource:etl_doris_tx_semaphore";
        this.collieDorisTxSemaphoreKey = "datakits:resource:collie_doris_tx_semaphore";
        this.dorisAvailableKey = "datakits:resource:doris_available";
    }

}
