package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author xxx
 * @since 2024/3/23
 */
public class IndexTypeSerializer extends JsonSerializer<IndexType> {
    @Override
    public void serialize(IndexType indexType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(IndexTypeStrategyFactory.getIndexUniqueKey(indexType.getDatabaseTypeEnum(), indexType.getIndexTypeName()));
    }
}
