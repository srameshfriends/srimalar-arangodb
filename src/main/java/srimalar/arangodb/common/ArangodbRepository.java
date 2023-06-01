package srimalar.arangodb.common;

public interface ArangodbRepository {
    String KEY = "ArangodbRepository";

    ArangodbExecutor getMaster();

    ArangodbExecutor getTransaction();
}
