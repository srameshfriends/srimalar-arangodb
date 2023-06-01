package srimalar.arangodb.common;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.StreamTransactionEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.util.RawBytes;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public final class ArangodbAuditLog {
    private final String collectionName;
    private ArangoDatabase database;
    private String userName;
    private boolean isCancelTransaction;
    private StreamTransactionEntity transactionEntity;

    public ArangodbAuditLog() {
        this.collectionName = getCollectionName();
    }

    private static String getCollectionName() {
        LocalDate localDate = LocalDate.now();
        return "log_" + localDate.getYear() + "_" + localDate.getMonth().name().substring(0, 3).toLowerCase();
    }

    public void setLogDatabase(ArangoDatabase auditLogDatabase) {
        this.database = auditLogDatabase;
        if (!database.exists()) {
            boolean status = database.create();
            log.info(status ? "Arango Log Database (" + database.name() + ") is created." : "ERROR : To create Arango Log Database (" + database.name() + ").");
        }

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void logForInsert(Object data) {
        log("I", data);
    }

    public void logForUpdate(Object data) {
        log("U", data);
    }

    public void logForDelete(Object data) {
        log("D", data);
    }

    private void log(String action, Object data) {
        AuditLog auditLog = new AuditLog();
        auditLog.setCreatedOn(LocalDateTime.now());
        auditLog.setCreatedBy(userName);
        auditLog.setAct(action);
        auditLog.setData(data);
        log(auditLog);
    }

    private String beginStreamTransaction() {
        if (transactionEntity == null) {
            ArangodbExecutor.StreamTransactionSet streamTransaction = new ArangodbExecutor.StreamTransactionSet(collectionName);
            transactionEntity = database.beginStreamTransaction(streamTransaction.getStreamTransactionOptions());
            streamTransaction.setTransactionEntity(transactionEntity);
        }
        return transactionEntity.getId();
    }

    public void abortStreamTransaction() {
        if (transactionEntity != null) {
            database.abortStreamTransaction(transactionEntity.getId());
        }
    }

    public void commitStreamTransaction() {
        if (isCancelTransaction) {
            abortStreamTransaction();
        } else if (transactionEntity != null) {
            database.commitStreamTransaction(transactionEntity.getId());
        }
    }

    public void cancelStreamTransaction() {
        this.isCancelTransaction = true;
    }

    public String getDatabaseName() {
        return database.name();
    }

    public DocumentEntity log(AuditLog auditLog) {
        ArangoCollection collection = getOrCreateCollection(collectionName);
        DocumentCreateOptions options = new DocumentCreateOptions().streamTransactionId(beginStreamTransaction());
        RawBytes rawBytes = ArangodbFactory.getRawBytes(auditLog);
        return collection.insertDocument(rawBytes, options, RawBytes.class);
    }

    public ArangoCollection getOrCreateCollection(String name) {
        ArangoCollection collection = database.collection(name);
        if (!collection.exists()) {
            collection.create();
        }
        return collection;
    }
}
