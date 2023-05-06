package srimalar.arangodb.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.time.LocalDateTime;

@JsonComponent
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>  {
    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) {
        if (value != null) {
            try {
                generator.writeString(FormatConstant.DATE_TIME_FORMATTER.format(value));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
