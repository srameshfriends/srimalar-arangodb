package srimalar.arangodb.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import srimalar.arangodb.converter.LocalDateDeserializer;
import srimalar.arangodb.converter.LocalDateSerializer;
import srimalar.arangodb.converter.LocalDateTimeDeserializer;
import srimalar.arangodb.converter.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonRootName("messages")
public class MessageProperty extends ArangoEntity {
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;

    @JsonProperty("index")
    private int index;

    @JsonProperty("serial_no")
    private long serialNo;

    @JsonProperty("rounding")
    private BigDecimal rounding;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("created_on")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdOn;

    @JsonProperty("updated_on")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updatedOn;

    public long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(long serialNo) {
        this.serialNo = serialNo;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BigDecimal getRounding() {
        return rounding;
    }

    public void setRounding(BigDecimal rounding) {
        this.rounding = rounding;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDate getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDate updatedOn) {
        this.updatedOn = updatedOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return new ToStringBuilder()
                .string("id", getId())
                .string("key", getKey())
                .string("rev", getRev())
                .string("locale", locale)
                .string("name", name)
                .string("value", value)
                .bigDecimal("rounding", rounding)
                .localDateTime("createdOn", createdOn)
                .localDate("updatedOn", updatedOn)
                .boolValue("active", active)
                .localDate("updatedOn", updatedOn)
                .intValue("index", index)
                .longValue("serialNo", serialNo)
                .toString();
    }
}
