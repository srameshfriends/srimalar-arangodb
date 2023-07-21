package srimalar.arangodb.common;

import java.util.HashMap;
import java.util.Map;

public class ArangoQuery {
    private final Class<?> type;
    private final String name;
    private final Map<String, Object> parameters;
    private String query;

    public ArangoQuery(Class<?> type) {
        this.name = ArangodbFactory.getCollectionName(type);
        this.parameters = new HashMap<>();
        this.type = type;
    }

    public ArangoQuery(String query, Class<?> type, Map<String, Object> parameter) {
        this.name = ArangodbFactory.getCollectionName(type);
        this.parameters = parameter;
        this.type = type;
        this.query = query;
    }

    public Class<?> getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public ArangoQuery setQuery(String query) {
        this.query = query;
        return ArangoQuery.this;
    }

    public String getName() {
        return name;
    }

    public ArangoQuery put(String name, Object param) {
        this.parameters.put(name, param);
        return ArangoQuery.this;
    }

    public Object remove(String name, Object param) {
        return this.parameters.remove(name);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return query == null ? "FOR x IN " + name + " RETURN x" : query;
    }
}
