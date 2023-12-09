package srimalar.arangodb.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDate;

@JsonComponent
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    private static final Logger logger = LoggerFactory.getLogger(LocalDateSerializer.class);

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) {
        if (value != null) {
            try {
                generator.writeString(FormatConstant.DATE_FORMATTER.format(value));
            } catch (Exception ex) {
                logger.warn("ERROR: LocalDateSerializer - serialize " + ex.getMessage());
            }
        }
    }
}
