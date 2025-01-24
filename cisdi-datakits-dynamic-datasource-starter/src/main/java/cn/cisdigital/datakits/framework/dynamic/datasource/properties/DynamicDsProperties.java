package cn.cisdigital.datakits.framework.dynamic.datasource.properties;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.druid.sql.parser.Token;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

/**
 * @author xxx
 * @since 2024-06-26
 */
@Data
@Slf4j
@Validated
@Component
@ConfigurationProperties(prefix = AutoConfigConstants.DYNAMIC_DS_CONFIG_PREFIX)
public class DynamicDsProperties {

    /**
     * 数据探查允许执行的语句
     */
    private List<Token> enableSqlToken;

    /**
     * 数据探查不允许执行的语句
     */
    private List<Token> disableSqlToken;




    public List<Token> getEnableSqlToken(){
        if(CollUtil.isEmpty(this.enableSqlToken)){
            log.warn("系统未配置enableSqlToken,采用默认配置");
            //给默认值
            return Arrays.asList(Token.SELECT,Token.SHOW,Token.WITH,Token.USE,Token.DESC,Token.EXPLAIN);
        }
        return this.enableSqlToken;
    }


    public List<Token> getDisableSqlToken(){
        if(CollUtil.isEmpty(this.disableSqlToken)){
            //给默认值
            log.warn("系统未配置disableSqlToken,采用默认配置");
            return Arrays.asList(Token.DROP,Token.ALTER);
        }
        return this.disableSqlToken;
    }


}
