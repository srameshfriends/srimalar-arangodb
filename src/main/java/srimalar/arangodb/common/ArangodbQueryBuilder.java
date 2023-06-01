package srimalar.arangodb.common;

import java.util.HashMap;
import java.util.Map;

public class ArangodbQueryBuilder {
    private final Class<?> type;
    private final StringBuilder builder;
    private final Map<String, Object> parameters;

    public ArangodbQueryBuilder(Class<?> type) {
        this.type = type;
        this.builder = new StringBuilder("FOR ");
        this.parameters = new HashMap<>();
        collection(ArangodbFactory.getCollectionName(type));
    }

    public Class<?> getType() {
        return type;
    }

    public ArangodbQueryBuilder collection(String name) {
        builder.append(" x IN ").append(name).append(" ");
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder collection(String name, String asName) {
        if(asName == null) {
            asName = "x";
        }
        builder.append(asName).append(" IN ").append(name).append(" ");
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder filter() {
        builder.append(" FILTER ");
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder build() {
        builder.append(" RETURN x");
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder build(String asName) {
        if(asName == null) {
            asName = "x";
        }
        builder.append(" RETURN ").append(asName);
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder append(String query) {
        builder.append(query);
        return ArangodbQueryBuilder.this;
    }

    public ArangodbQueryBuilder add(String name, Object param) {
        this.parameters.put(name, param);
        return ArangodbQueryBuilder.this;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
