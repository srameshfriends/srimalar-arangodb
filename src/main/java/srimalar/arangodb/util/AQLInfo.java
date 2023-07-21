package srimalar.arangodb.util;

import java.util.Objects;

public class AQLInfo {
    private final String name, description, fileName, query;

    public AQLInfo(String name, String description, String fileName) {
        this.name = name;
        this.query = null;
        this.description = description;
        this.fileName = fileName;
    }

    public AQLInfo(String name, String description, String fileName, String query) {
        this.name = name;
        this.description = description;
        this.fileName = fileName;
        this.query = query;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AQLInfo sqlModel = (AQLInfo) o;
        return Objects.equals(name, sqlModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public AQLInfo clone(String query) {
        return new AQLInfo(name, description, fileName, query);
    }
}
