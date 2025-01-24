package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author xxx
 */
public class ColumnTypeSerializer extends JsonSerializer<ColumnType> {
	@Override
	public void serialize(ColumnType columnType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(((Enum) columnType).name());
	}
}
