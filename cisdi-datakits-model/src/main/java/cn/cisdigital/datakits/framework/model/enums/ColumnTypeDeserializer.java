package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * @author xxx
 */
@Slf4j
public class ColumnTypeDeserializer extends JsonDeserializer<ColumnType> {

    @Override
    public ColumnType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            return ColumnTypeStrategyFactory.getColumnType(jsonParser.getText());
        } catch (Exception e) {
            log.error("ColumnType类型反序列化失败！value={}", jsonParser.getText());
            return null;
        }
    }
}


