package cn.cisdigital.datakits.framework.crypto.config;

import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConstants;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;

/**
 * 算法配置自定义反序列化
 *
 * @author xxx
 * @since 2023-11-21
 */
public class CryptoConfigDeserializer extends JsonDeserializer<CryptoConfig> {

    @Override
    public CryptoConfig deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // key和iv需要Base64解码
        JsonNode keyNode = node.get(CryptoConstants.KEY);
        JsonNode ivNode = node.get(CryptoConstants.IV);

        if (keyNode != null && keyNode.isTextual()) {
            String encodedKey = keyNode.asText();
            byte[] keyBytes = Base64.decode(encodedKey);
            // 重新设置keyNode的值
            ((ObjectNode) node).put(CryptoConstants.KEY, keyBytes);
        }

        if (ivNode != null && ivNode.isTextual()) {
            String encodedIv = ivNode.asText();
            byte[] ivBytes = Base64.decode(encodedIv);
            // 重新设置keyNode的值
            ((ObjectNode) node).put(CryptoConstants.IV, ivBytes);
        }

        CryptoTypeEnum cryptoType = CryptoTypeEnum.valueOf(node.get(CryptoConstants.CRYPTO_TYPE).asText());
        // 根据cryptoType确定要实例化的具体CryptoConfig实现类
        return JsonUtils.parseObject(node, cryptoType.getCryptoConfigClass());
    }
}
