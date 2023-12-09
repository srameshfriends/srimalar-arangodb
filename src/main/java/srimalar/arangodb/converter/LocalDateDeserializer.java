package srimalar.arangodb.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@JsonComponent
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        String text = jp.getText();
        if (text != null) {
            try {
                return LocalDate.parse(text, FormatConstant.DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                //ignore exception
            }
        }
        return null;
    }
}
