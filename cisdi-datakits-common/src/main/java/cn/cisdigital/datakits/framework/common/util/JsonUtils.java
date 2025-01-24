package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.common.serialize.PagePropertyFilterMinIn;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * 基于Jackson的JSON工具
 *
 * @author xxx
 * @since 2022-09-25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String ASIA_SHANGHAI = "Asia/Shanghai";

    static {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(ASIA_SHANGHAI));

        // 自动注册jdk8数据类型、localdatetime、afterburner等模块
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.setDateFormat(dateFormat);
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone(ASIA_SHANGHAI));
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 支持@RequestBody反序列化枚举时，前端传空字符串
        OBJECT_MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        // 序列换成json时,将所有的long变成string.因为js中得数字类型不能包含所有的java long值，超过16位后会出现精度丢失
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance);
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        simpleModule.addDeserializer(Long.class, new LongDeserializer());
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        OBJECT_MAPPER.registerModule(simpleModule);

        // 忽略mybaits-plus Page对象的setPages
        FilterProvider filters = new SimpleFilterProvider().addFilter("PageFilter", SimpleBeanPropertyFilter.serializeAllExcept("pages"));
        OBJECT_MAPPER.setFilterProvider(filters);
        OBJECT_MAPPER.setMixIns(new HashMap<Class<?>, Class<?>>(){{put(Object.class, PagePropertyFilterMinIn.class);}});
    }

    /**
     * 将对象转换为JSON String
     *
     * @param o 被转换的对象
     * @return JSON String
     * @throws JsonProcessingException 转换失败
     */
    public static String toJsonString(Object o) throws JsonProcessingException {
        if (o == null) {
            return null;
        }
        return OBJECT_MAPPER.writeValueAsString(o);
    }

    public static JsonNode readTree(String jsonStr) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(jsonStr);
    }


    /**
     * 将JSON String转换为对象
     *
     * @param jsonStr       json数据
     * @param typeReference 目标对象类型
     * @param <T>           目标对象泛型
     * @return 目标对象
     * @throws JsonProcessingException 转换失败
     */
    public static <T> T parseObject(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
        if (CharSequenceUtil.isBlank(jsonStr) || typeReference == null) {
            return null;
        }
        return OBJECT_MAPPER.readValue(jsonStr, typeReference);
    }

    public static <T> T parseObject(JsonNode node, Class<T> clazz) throws JsonProcessingException {
        if (node == null || clazz == null) {
            return null;
        }
        return OBJECT_MAPPER.treeToValue(node, clazz);
    }

    /**
     * 将JSON String转换为对象
     *
     * @param jsonStr json数据
     * @param clazz 目标对象类类型
     * @param <T> 目标对象泛型
     * @return 目标对象
     * @throws JsonProcessingException 转换失败
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        if (CharSequenceUtil.isBlank(jsonStr) || clazz == null) {
            return null;
        }
        return OBJECT_MAPPER.readValue(jsonStr, clazz);
    }

    /**
     * 将JSON Array String转换为列表
     *
     * @param listJsonStr json array数据
     * @param elementClazz 元素类类型
     * @param <T> 元素对象泛型
     * @return 目标类型列表
     * @throws JsonProcessingException 转换失败
     */
    public static <T> List<T> parseList(String listJsonStr, Class<T> elementClazz) throws JsonProcessingException {
        if (CharSequenceUtil.isBlank(listJsonStr) || elementClazz == null) {
            return Collections.emptyList();
        }
        CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, elementClazz);
        return OBJECT_MAPPER.readValue(listJsonStr, collectionType);
    }

    /**
     * 将JSON Map String转换为Map
     *
     * @param mapJsonStr json map数据
     * @param keyClass map key类类型
     * @param valueClass map value类类型
     * @param <K> map key类泛型
     * @param <V> map value类泛型
     * @return 目标类型Map
     * @throws JsonProcessingException 转换失败
     */
    public static <K, V> Map<K, V> parseMap(String mapJsonStr, Class<K> keyClass, Class<V> valueClass)
        throws JsonProcessingException {
        if (CharSequenceUtil.isBlank(mapJsonStr) || keyClass == null || valueClass == null) {
            return Collections.emptyMap();
        }
        MapType mapType = TypeFactory.defaultInstance().constructMapType(Map.class, keyClass, valueClass);
        return OBJECT_MAPPER.readValue(mapJsonStr, mapType);
    }

    public static class LongDeserializer extends JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
            String string = jsonParser.getValueAsString();
            if (CharSequenceUtil.isBlank(string)) {
                return null;
            }
            return jsonParser.getValueAsLong();
        }
    }

    public static class StayLongSerializer extends JsonSerializer<Long> {

        @Override
        public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
            jsonGenerator.writeNumber(aLong);
        }
    }

    /**
     * LocalDateTime全局序列化为时间戳
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
    }

    /**
     * 反序列化时间戳为LocalDateTime
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
            throws IOException {
            ZoneOffset zoneOffset = ZoneOffset.of("+8");
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), zoneOffset);
        }
    }

    /**
     * LocalDate全局序列化为时间戳
     */
    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
            ZoneId zoneId = ZoneId.of(ASIA_SHANGHAI);
            jsonGenerator.writeNumber(localDate.atStartOfDay(zoneId).toInstant().toEpochMilli());
        }
    }


    /**
     * 反序列化LocalDate
     */
    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext deserializationContext)
            throws IOException {
            ZoneId zoneId = ZoneId.of(ASIA_SHANGHAI);
            return Instant.ofEpochMilli(p.getValueAsLong()).atZone(zoneId).toLocalDate();
        }
    }
}
