package srimalar.arangodb.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Objects;

@JsonRootName("messages")
public class MessageProperty extends ArangodbEntity {
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessageProperty prop = (MessageProperty) obj;
        return Objects.equals(prop.locale, locale) && Objects.equals(prop.name, name) && Objects.equals(prop.value, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locale, name, value);
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
                .toString();
    }
}
