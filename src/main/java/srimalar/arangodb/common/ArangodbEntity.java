package srimalar.arangodb.common;

import com.arangodb.serde.jackson.Id;
import com.arangodb.serde.jackson.Key;
import com.arangodb.serde.jackson.Rev;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public abstract class ArangodbEntity implements EntityIdentity {
    @Id
    @JsonProperty("_id")
    private String id;
    @Key
    @JsonProperty("_key")
    private String key;
    @Rev
    @JsonProperty("_rev")
    private String rev;

    public ArangodbEntity() {
    }

    public ArangodbEntity(String key, String id, String rev) {
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
        ArangodbEntity that = (ArangodbEntity) o;
        if (id == null || that.id == null) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder().string("id", id).string("rev", rev).toString();
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return key == null || key.isBlank();
    }
}
