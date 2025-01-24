package cn.cisdigital.datakits.framework.common.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author xxx
 * @since 2024-04-07
 */
@Slf4j
class JsonUtilsTest {

    public static final String JSON_STR = "{\"longValue\":\"12\",\"time\":1712477995775}";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    private static class TestVo {

        private Long longValue;
        private LocalDateTime time;
    }

    @Test
    void success_deserialize() {
        assertDoesNotThrow(() -> {
            JsonNode jsonNode = JsonUtils.readTree(JSON_STR);
            JsonNode longValueNode = jsonNode.get("longValue");
            JsonNode timeNode = jsonNode.get("time");
            assertNotNull(longValueNode);
            assertNotNull(timeNode);
            String longValueStr = longValueNode.asText();
            LocalDateTime localDateTime = JsonUtils.OBJECT_MAPPER.convertValue(timeNode, LocalDateTime.class);
            assertEquals("12", longValueStr);
            assertEquals(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(1712477995775L), TimeZone.getDefault().toZoneId()),
                localDateTime);
        });
    }

    @Test
    void success_serialize() {
        assertDoesNotThrow(() -> {
            String s = JsonUtils.toJsonString(new TestVo().setLongValue(12L).setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(1712477995775L), TimeZone.getDefault().toZoneId())));
            assertNotNull(s);
            assertEquals(JSON_STR, s);
        });
    }

    @Test
    void success_normal_enum() throws JsonProcessingException {
        String s = JsonUtils.toJsonString(new TestClass().setTestEnum(TestEnum.ONE));
        System.out.println(s);

        TestClass testClass = JsonUtils.parseObject(s.toLowerCase(), TestClass.class);
        System.out.println(JsonUtils.toJsonString(testClass));

    }

    @Test
    void success_enum() throws JsonProcessingException {

        System.out.println(JsonUtils.parseObject("{\"number\": \"\",\"testEnum\":1}", TestClass2.class));
    }

    @Test
    void testTypeReference() throws JsonProcessingException{
        List<TestClass3<String>> list = new ArrayList<>();
        list.add(new TestClass3<String>().setData("123").setTestEnum2(TestEnum2.ONE));
        list.add(new TestClass3<String>().setData("456").setTestEnum2(TestEnum2.TWO));
        String jsonString = JsonUtils.toJsonString(list);
        System.out.println(jsonString);
        List<TestClass3<String>> testClass3s = JsonUtils.parseObject(jsonString, new TypeReference<List<TestClass3<String>>>() {
        });
        System.out.println(JsonUtils.toJsonString(testClass3s));
    }

    @Data
    @Accessors(chain = true)
    private static class TestClass3<T> {
        private T data;

        private TestEnum2 testEnum2;
    }


    @Data
    @Accessors(chain = true)
    private static class TestClass {

        @JsonDeserialize()
        private TestEnum testEnum;
    }

    @Data
    @Accessors(chain = true)
    private static class TestClass2 {


        private long number;

        @JsonDeserialize()
        private TestEnum2 testEnum;
    }

    private static class myDeserializer extends JsonDeserializer<TestEnum> {

        @Override
        public TestEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {

            return null;
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum TestEnum {

        ONE("one", "sdaff"),
        TWO("two", "sadjflasd"),
        ;

        private final String code;
        private final String desc;
    }


    @Getter
    @RequiredArgsConstructor
    private enum TestEnum2 implements BaseEnum {

        ONE(1, "sdaff"),
        TWO(2, "sadjflasd"),
        ;

        private final int code;
        private final String key;
    }
}
