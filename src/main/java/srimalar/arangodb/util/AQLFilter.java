package srimalar.arangodb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AQLFilter {
    private final StringBuilder builder;
    private final Map<String, Object> parameter;

    public AQLFilter() {
        this.builder = new StringBuilder();
        this.parameter = new HashMap<>();
    }

    public AQLFilter append(String query) {
        this.builder.append(query);
        return AQLFilter.this;
    }

    public AQLFilter and(String query) {
        this.builder.append(" AND ").append(query);
        return AQLFilter.this;
    }

    public AQLFilter and(String query, String variable, Object param) {
        this.builder.append(" AND ").append(query).append(" == @").append(variable);
        this.parameter.put(variable, param);
        return AQLFilter.this;
    }

    public AQLFilter in(String query, List<Object> array) {
        if (array == null || array.isEmpty()) {
            return AQLFilter.this;
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            sb.append("'").append(obj).append("',");
        }
        String variable = sb.substring(0, sb.length() - 1);
        this.builder.append(query).append(" IN [ ").append(variable).append(" ] ");
        return AQLFilter.this;
    }

    public AQLFilter in(String query, Object[] array) {
        if (array == null || 0 == array.length) {
            return AQLFilter.this;
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : array) {
            sb.append("'").append(obj).append("',");
        }
        String variable = sb.substring(0, sb.length() - 1);
        this.builder.append(query).append(" IN [ ").append(variable).append(" ] ");
        return AQLFilter.this;
    }

    public AQLFilter or(String query) {
        this.builder.append(" OR ").append(query);
        return AQLFilter.this;
    }

    public AQLFilter searchText(String text, String... fields) {
        if (text == null || text.isBlank() || fields == null) {
            return AQLFilter.this;
        }
        if (!text.contains("%")) {
            text = "%" + text + "%";
        }
        String vSearchText = "vSearchText";
        StringBuilder sb = new StringBuilder();
        for (String att : fields) {
            sb.append(" OR LIKE(").append(att).append(", @").append(vSearchText).append(", true)");
        }
        String qry = sb.toString();
        qry = qry.replaceFirst(" OR ", " ");
        builder.append(" AND (").append(qry).append(") ");
        parameter.put(vSearchText, text);
        return AQLFilter.this;
    }

    private String capitalized(String str) {
        return str.isEmpty() ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public Map<String, Object> getParameter() {
        return this.parameter;
    }

    @Override
    public String toString() {
        String query = builder.toString().trim();
        if (query.isBlank()) {
            return "";
        }
        if (query.startsWith("AND ")) {
            query = query.replaceFirst("AND ", " FILTER ");
        } else if (query.startsWith("OR ")) {
            query = query.replaceFirst("OR ", " FILTER ");
        } else if (!query.trim().startsWith("FILTER ") || !query.trim().startsWith("filter ")) {
            query = " FILTER " + query;
        }
        return query;
    }
}
