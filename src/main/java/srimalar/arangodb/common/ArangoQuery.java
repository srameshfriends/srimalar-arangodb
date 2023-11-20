package srimalar.arangodb.common;

import srimalar.arangodb.util.AQLBuilder;

import java.util.HashMap;
import java.util.Map;

public class ArangoQuery {
    private final Class<?> type;
    private final String name;
    private Map<String, Object> parameters;
    private String query;

    public ArangoQuery(Class<?> type) {
        this.name = ArangodbFactory.getCollectionName(type);
        this.parameters = new HashMap<>();
        this.type = type;
    }

    public ArangoQuery(String query, Class<?> type, Map<String, Object> parameter) {
        this.name = ArangodbFactory.getCollectionName(type);
        this.parameters = parameter == null ? new HashMap<>() : parameter;
        this.type = type;
        this.query = query;
    }

    public Class<?> getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setQuery(String query, Map<String, Object> param) {
        this.query = query;
        this.parameters = param == null ? new HashMap<>() : param;
    }

    public void setQuery(String query, String variable, Object param) {
        this.query = query;
        parameters.put(variable, param);
    }

    public ArangoQuery build(AQLBuilder builder) {
        this.query = builder.toString();
        this.parameters.putAll(builder.getParameter());
        return ArangoQuery.this;
    }

    public ArangoQuery count(AQLBuilder builder) {
        this.query = builder.getCountQuery();
        this.parameters.putAll(builder.getParameter());
        return ArangoQuery.this;
    }

    public String getName() {
        return name;
    }

    public ArangoQuery put(String name, Object param) {
        this.parameters.put(name, param);
        return ArangoQuery.this;
    }

    public ArangoQuery putAll(Map<String, Object> map) {
        if (map != null) {
            this.parameters.putAll(map);
        }
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
