package srimalar.arangodb.common;

import com.arangodb.serde.jackson.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public abstract class ArangoEntity implements EntityIdentity {
    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    public ArangoEntity() {
    }

    public ArangoEntity(String key, String id, String rev) {
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
        ArangoEntity that = (ArangoEntity) o;
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
        return key != null;
    }
}
