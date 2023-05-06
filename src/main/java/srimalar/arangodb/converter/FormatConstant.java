package srimalar.arangodb.converter;

import java.time.format.DateTimeFormatter;

public interface FormatConstant {
    DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
