package srimalar.arangodb.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import srimalar.core.converter.LocalDateTimeDeserializer;
import srimalar.core.converter.LocalDateTimeSerializer;
import srimalar.core.model.EntityIdentity;
import srimalar.core.model.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonRootName("audit_log")
public class AuditLog implements EntityIdentity {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("_key")
    private String key;

    @JsonProperty("_rev")
    private String rev;

    @JsonProperty("created_on")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdOn;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("act")
    private String act;

    public AuditLog() {
    }

    public AuditLog(String key, String id, String rev) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityIdentity that = (EntityIdentity) o;
        if (id == null || that.getId() == null) return false;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder().string("_id", id)
                .string("_key", key).string("_rev", rev).toString();
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return key == null || key.isBlank();
    }
}
