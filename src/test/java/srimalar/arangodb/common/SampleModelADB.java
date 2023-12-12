package srimalar.arangodb.common;

import com.arangodb.serde.jackson.Id;
import com.arangodb.serde.jackson.Key;
import com.arangodb.serde.jackson.Rev;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import srimalar.core.model.MessageEntity;
import srimalar.core.model.ToStringBuilder;

import java.util.Objects;

@JsonRootName("messages")
public class SampleModelADB implements MessageEntity {
    @Id
    @JsonProperty("_id")
    private String id;
    @Key
    @JsonProperty("_key")
    private String key;
    @Rev
    @JsonProperty("_rev")
    private String rev;

    @JsonProperty("locale")
    private String locale;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;

    public SampleModelADB() {
    }

    public SampleModelADB(String key, String id, String rev) {
        this.key = key;
        this.id = id;
        this.rev = rev;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getRev() {
        return rev;
    }

    @Override
    public void setRev(String rev) {
        this.rev = rev;
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

    @Override
    @JsonIgnore
    public boolean isNew() {
        return key == null || key.isBlank();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SampleModelADB prop = (SampleModelADB) obj;
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
