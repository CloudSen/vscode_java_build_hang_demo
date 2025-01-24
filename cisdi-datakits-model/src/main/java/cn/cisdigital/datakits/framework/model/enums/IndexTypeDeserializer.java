package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * @author xxx
 * @since 2023/7/19
 */
@Slf4j
public class IndexTypeDeserializer extends JsonDeserializer<IndexType> {

    @Override
    public IndexType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            return IndexTypeStrategyFactory.getIndexType(jsonParser.getText());
        } catch (Exception e) {
            log.error("IndexType类型反序列化失败！value={}", jsonParser.getText());
            return null;
        }
    }
}


