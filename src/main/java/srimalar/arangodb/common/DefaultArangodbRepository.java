package srimalar.arangodb.common;

import com.arangodb.ArangoDB;

public class DefaultArangodbRepository implements ArangodbRepository {
    private final ArangodbExecutor master, transaction;

    public DefaultArangodbRepository(ArangoDB arangoDB, String dbName) {
        if (dbName == null || 10 < dbName.length()) {
            throw new IllegalArgumentException("Database name should not be null or maximum allowed chars is 10 (" + dbName + ")");
        }
        ArangodbAuditLog arangodbAuditLog = new ArangodbAuditLog();
        arangodbAuditLog.setLogDatabase(arangoDB.db(dbName + "_al"));
        //
        this.master = new ArangodbExecutor();
        this.master.setDatabase(arangoDB.db(dbName + "_ma"), arangodbAuditLog);
        //
        this.transaction = new ArangodbExecutor();
        this.transaction.setDatabase(arangoDB.db(dbName + "_tr"), arangodbAuditLog);
    }

    @Override
    public ArangodbExecutor getMaster() {
        return master;
    }

    @Override
    public ArangodbExecutor getTransaction() {
        return transaction;
    }
}
