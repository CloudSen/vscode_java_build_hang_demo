package cn.cisdigital.datakits.framework.crypto.config;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.util.Objects;

/**
 * 算法配置自定义序列化
 *
 * @author xxx
 * @since 2023-11-24
 */
public class CryptoConfigSerializer extends JsonSerializer<CryptoConfig> {

    @Override
    public void serialize(CryptoConfig cryptoConfig, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (Objects.nonNull(cryptoConfig.getCryptoType())) {
            jsonGenerator.writeStringField(CryptoConstants.CRYPTO_TYPE, cryptoConfig.getCryptoType().name());
        }
        if (Objects.nonNull(cryptoConfig.getMode())) {
            jsonGenerator.writeStringField(CryptoConstants.MODE, cryptoConfig.getMode().name());
        }
        if (Objects.nonNull(cryptoConfig.getKey())) {
            jsonGenerator.writeStringField(CryptoConstants.KEY, Base64.toBase64String(cryptoConfig.getKey()));
        }
        if (Objects.nonNull(cryptoConfig.getKeySize())) {
            jsonGenerator.writeNumberField(CryptoConstants.KEY_SIZE, cryptoConfig.getKeySize());
        }
        if (Objects.nonNull(cryptoConfig.getPadding())) {
            jsonGenerator.writeStringField(CryptoConstants.PADDING, cryptoConfig.getPadding().name());
        }
        if (Objects.nonNull(cryptoConfig.getIv())) {
            jsonGenerator.writeStringField(CryptoConstants.IV, Base64.toBase64String(cryptoConfig.getIv()));
        }
        if (Objects.nonNull(cryptoConfig.getIvSize())) {
            jsonGenerator.writeNumberField(CryptoConstants.IV_SIZE, cryptoConfig.getIvSize());
        }
        if (Objects.nonNull(cryptoConfig.getRandomIv())) {
            jsonGenerator.writeBooleanField(CryptoConstants.RANDOM_IV, cryptoConfig.getRandomIv());
        }
        if (Objects.nonNull(cryptoConfig.getSalt())) {
            jsonGenerator.writeStringField(CryptoConstants.SALT, cryptoConfig.getSalt());
        }
        if (Objects.nonNull(cryptoConfig.getSaltSize())) {
            jsonGenerator.writeNumberField(CryptoConstants.SALT_SIZE, cryptoConfig.getSaltSize());
        }
        if (Objects.nonNull(cryptoConfig.getRandomSalt())) {
            jsonGenerator.writeBooleanField(CryptoConstants.RANDOM_SALT, cryptoConfig.getRandomSalt());
        }
        if (Objects.nonNull(cryptoConfig.getMaskingCharacter())) {
            jsonGenerator.writeStringField(CryptoConstants.MASKING_CHARACTER, cryptoConfig.getMaskingCharacter());
        }
        if (Objects.nonNull(cryptoConfig.getMaskingMode())) {
            jsonGenerator.writeStringField(CryptoConstants.MASKING_MODE, cryptoConfig.getMaskingMode().name());
        }
        if (Objects.nonNull(cryptoConfig.getMaskingValue())) {
            jsonGenerator.writeStringField(CryptoConstants.MASKING_VALUE, cryptoConfig.getMaskingValue());
        }
        if (Objects.nonNull(cryptoConfig.getMaskingResultSize())) {
            jsonGenerator.writeNumberField(CryptoConstants.MASKING_RESULT_SIZE, cryptoConfig.getMaskingResultSize());
        }
        jsonGenerator.writeEndObject();
    }
}
