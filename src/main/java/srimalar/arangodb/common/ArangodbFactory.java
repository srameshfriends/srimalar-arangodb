package srimalar.arangodb.common;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.MultiDocumentEntity;
import com.arangodb.serde.jackson.JacksonSerde;
import com.arangodb.util.RawBytes;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import srimalar.arangodb.converter.LocalDateDeserializer;
import srimalar.arangodb.converter.LocalDateSerializer;
import srimalar.arangodb.converter.LocalDateTimeDeserializer;
import srimalar.arangodb.converter.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ArangodbFactory {
    private static final JacksonSerde JACKSON_SERDE = JacksonSerde.create(JsonMapper.builder().addModule(getJodaModule()).build());
    private static final ConcurrentMap<Class<?>, String> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();

    private static JodaModule getJodaModule() {
        JodaModule jodaModule = new JodaModule();
        jodaModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        jodaModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        jodaModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        return jodaModule;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(getJavaTimeModule());

    public static JavaTimeModule getJavaTimeModule() {
        JavaTimeModule jodaModule = new JavaTimeModule();
        jodaModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        jodaModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        jodaModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        return jodaModule;
    }
    public static RawBytes getRawBytes(Object value) {
        return RawBytes.of(JACKSON_SERDE.serialize(value));
    }

    public static RawBytes getRawBytes(Map<String, Object> map) {
        Byte[] result = OBJECT_MAPPER.convertValue(map, Byte[].class);
        return RawBytes.of(JACKSON_SERDE.serialize(result));
    }

    public static Object getTypeObject(BaseDocument document, Class<?> clazz) {
        return OBJECT_MAPPER.convertValue(document, clazz);
    }

    public static String getCollectionName(Class<?> clazz) {
        if(ENTITY_CLASS_MAP.containsKey(clazz)) {
            return ENTITY_CLASS_MAP.get(clazz);
        }
        JsonRootName rootName = clazz.getDeclaredAnnotation(JsonRootName.class);
        if(rootName == null) {
            log.error("ERROR: Arango entity @JsonRootName annotation not found (" + clazz + ")");
            throw new RuntimeException("ERROR: Arango entity @JsonRootName annotation not found (" + clazz + ")");
        }
        if(rootName.value().isBlank()) {
            ENTITY_CLASS_MAP.put(clazz, clazz.getSimpleName().trim().toLowerCase());
        } else {
            ENTITY_CLASS_MAP.put(clazz, rootName.value().trim().toLowerCase());
        }
        return ENTITY_CLASS_MAP.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(RawBytes rawBytes, Class<?> type) {
        try {
            return (T)OBJECT_MAPPER.readValue(rawBytes.get(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getNewInstanceList(MultiDocumentEntity<DocumentCreateEntity<RawBytes>> entity, Class<?> type) {
        List<T> resultList = new ArrayList<>();
        entity.getDocuments().forEach(rawBytes -> {
            T result = ArangodbFactory.getObject(rawBytes.getNew(), type);
            resultList.add(result);
        });
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNewInstance(DocumentEntity entity, Object obj) {
        if (obj instanceof ArangodbEntity arangoEntity) {
            arangoEntity.setId(entity.getId());
            arangoEntity.setKey(entity.getKey());
            arangoEntity.setRev(entity.getRev());
        }
        return (T) getNewInstance(obj);
    }

    public static Object getNewInstance(Object obj) {
        return JACKSON_SERDE.deserialize(JACKSON_SERDE.serialize(obj), obj.getClass());
    }

    public static ObjectNode getJsonObjectNode(Object obj) {
        if (obj instanceof ObjectNode node) {
            return node;
        }
        return JACKSON_SERDE.deserialize(JACKSON_SERDE.serialize(obj), ObjectNode.class);
    }
}
