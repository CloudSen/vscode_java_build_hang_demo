package unit.config;

import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.config.CryptoConfigDeserializer;
import cn.cisdigital.datakits.framework.crypto.config.CryptoConfigSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeAll;

public class SerializeBaseTest {

    public static final ObjectMapper TEST_OBJECT_MAPPER = JsonUtils.OBJECT_MAPPER;

    @BeforeAll
    static void postProcessObjectMapper() {
        // 注册 CryptoConfigSerializer 作为 CryptoConfig 类的自定义序列化器
        TEST_OBJECT_MAPPER
                .registerModule(new SimpleModule().addSerializer(CryptoConfig.class, new CryptoConfigSerializer()));
        TEST_OBJECT_MAPPER
                .registerModule(new SimpleModule().addDeserializer(CryptoConfig.class, new CryptoConfigDeserializer()));
    }
}
