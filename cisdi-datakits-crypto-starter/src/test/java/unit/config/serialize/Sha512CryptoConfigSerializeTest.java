package unit.config.serialize;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512.Sha512CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import unit.config.SerializeBaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sha512CryptoConfigSerializeTest extends SerializeBaseTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void success_sha512_random_salt_deserialize(boolean isRandomSalt) {
        assertDoesNotThrow(() -> {
            CryptoConfig cryptoConfig = new Sha512CryptoConfig()
                    .setRandomSalt(isRandomSalt)
                    .setSalt("123")
                    .setSaltSize(6);
            System.out.println("原始配置：");
            System.out.println(cryptoConfig);

            String json = TEST_OBJECT_MAPPER.writeValueAsString(cryptoConfig);
            System.out.println("原始配置序列化json：");
            System.out.println(json);
            if (isRandomSalt) {
                assertThat(json).isEqualTo("{\"cryptoType\":\"SHA512\",\"salt\":\"123\",\"saltSize\":6,\"randomSalt\":true}");
            } else {
                assertThat(json).isEqualTo("{\"cryptoType\":\"SHA512\",\"salt\":\"123\",\"saltSize\":6,\"randomSalt\":false}");
            }

            JsonNode node = TEST_OBJECT_MAPPER.readTree(json);
            CryptoTypeEnum cryptoType = CryptoTypeEnum.valueOf(node.get("cryptoType").asText());
            CryptoConfig deserializedCryptoConfig = TEST_OBJECT_MAPPER.treeToValue(node,
                    cryptoType.getCryptoConfigClass());
            System.out.println("原始配置反序列化：");
            System.out.println(deserializedCryptoConfig);
            assertEquals(cryptoConfig, deserializedCryptoConfig);
        });
    }

    @Test
    void success_serialize_sha512_fix_salt() {
        assertDoesNotThrow(() -> {
            CryptoConfig cryptoConfig = new Sha512CryptoConfig()
                    .setSalt("123");
            System.out.println("原始配置：");
            System.out.println(cryptoConfig);

            // 序列化
            String json = TEST_OBJECT_MAPPER.writeValueAsString(cryptoConfig);
            System.out.println("原始配置序列化json：");
            System.out.println(json);
            assertThat(json).isEqualTo("{\"cryptoType\":\"SHA512\",\"salt\":\"123\",\"saltSize\":8,\"randomSalt\":false}");

            // 反序列化
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
