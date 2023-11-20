package srimalar.arangodb.util;

import java.util.HashMap;
import java.util.Map;

public class AQLBuilder {
    private final StringBuilder builder;
    private final Map<String, Object> parameter;

    public AQLBuilder() {
        this.builder = new StringBuilder();
        this.parameter = new HashMap<>();
    }

    public AQLBuilder append(String query) {
        this.builder.append(query);
        return AQLBuilder.this;
    }

    public AQLBuilder append(String query, String variable, Object param) {
        this.builder.append(query);
        this.parameter.put(variable, param);
        return AQLBuilder.this;
    }

    public AQLBuilder append(AQLFilter aqlFilter) {
        this.builder.append(aqlFilter);
        this.parameter.putAll(aqlFilter.getParameter());
        return AQLBuilder.this;
    }

    public AQLBuilder append(AQLSort aqlSort) {
        this.builder.append(aqlSort);
        return AQLBuilder.this;
    }

    public AQLBuilder put(String variable, Object param) {
        this.parameter.put(variable, param);
        return AQLBuilder.this;
    }

    public AQLBuilder putAll(Map<String, Object> parameter) {
        if (parameter != null) {
            this.parameter.putAll(parameter);
        }
        return AQLBuilder.this;
    }

    public String getCountQuery() {
        return builder + " COLLECT WITH COUNT INTO length RETURN length";
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return builder.toString().trim();
    }
}
