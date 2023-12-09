package srimalar.arangodb.util;

public class AQLSort {
    public static final int LIMIT = 100;
    private final StringBuilder builder;
    private int limiting;
    private long offsetIndex;
    private boolean isOffSet, isLimit;

    public AQLSort() {
        builder = new StringBuilder();
    }

    public AQLSort sort(String defaultSort, String... sortQuery) {
        if (sortQuery == null || 0 == sortQuery.length) {
            sortQuery = new String[]{defaultSort};
        }
        builder.append(String.join(", ", sortQuery));
        return AQLSort.this;
    }

    public AQLSort limit(int limit) {
        if (1 > limit || 1000 < limit) {
            limit = LIMIT;
        }
        this.isOffSet = true;
        this.isLimit = true;
        this.limiting = limit;
        return AQLSort.this;
    }

    public AQLSort limit(int limit, int defaultLimit) {
        if (1 > limit || 1000 < limit) {
            if (1 > defaultLimit || 1000 < defaultLimit) {
                limit = LIMIT;
            } else {
                limit = defaultLimit;
            }
        }
        this.limiting = limit;
        this.isOffSet = true;
        this.isLimit = true;
        return AQLSort.this;
    }

    public AQLSort offset(long offset) {
        this.offsetIndex = offset;
        isOffSet = true;
        return AQLSort.this;
    }

    @Override
    public String toString() {
        if (builder.isEmpty()) {
            return "";
        }
        String query = " SORT " + builder;
        if (isOffSet && !isLimit) {
            return query + " LIMIT " + offsetIndex;
        }
        if (isLimit) {
            return query + " LIMIT " + offsetIndex + ", " + limiting;
        }
        return query;
    }

    /*public static String getArangoQuery(Pagination paging, String sortDefault, String... sortQuery) {
        if(sortQuery == null) {
            sortQuery = new String[]{sortDefault};
        }
        return " SORT " + String.join(", ", sortQuery) + " LIMIT " + paging.getOffset() + ", " + paging.getLimit();
    }*/
}
