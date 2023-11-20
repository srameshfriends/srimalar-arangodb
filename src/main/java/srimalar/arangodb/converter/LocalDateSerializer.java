package srimalar.arangodb.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDate;

@Slf4j
@JsonComponent
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) {
        if (value != null) {
            try {
                generator.writeString(FormatConstant.DATE_FORMATTER.format(value));
            } catch (Exception ex) {
                log.warn("ERROR: LocalDateSerializer - serialize " + ex.getMessage());
            }
        }
    }
}
