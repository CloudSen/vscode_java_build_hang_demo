package unit.config.serialize;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import unit.config.SerializeBaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base64CryptoConfigSerializeTest extends SerializeBaseTest {

    @Test
    void success_base64_random_salt_deserialize() {
        assertDoesNotThrow(() -> {
            CryptoConfig cryptoConfig = new Base64CryptoConfig();
            System.out.println("原始配置：");
            System.out.println(cryptoConfig);

            String json = TEST_OBJECT_MAPPER.writeValueAsString(cryptoConfig);
            System.out.println("原始配置序列化json：");
            System.out.println(json);
            assertThat(json).isEqualTo("{\"cryptoType\":\"BASE64\"}");

            JsonNode node = TEST_OBJECT_MAPPER.readTree(json);
            CryptoTypeEnum cryptoType = CryptoTypeEnum.valueOf(node.get("cryptoType").asText());
            CryptoConfig deserializedCryptoConfig = TEST_OBJECT_MAPPER.treeToValue(node,
                    cryptoType.getCryptoConfigClass());
            System.out.println("原始配置反序列化：");
            System.out.println(deserializedCryptoConfig);
            assertEquals(cryptoConfig, deserializedCryptoConfig);
        });
    }
}
