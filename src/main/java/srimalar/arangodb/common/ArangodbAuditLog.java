package srimalar.arangodb.common;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.StreamTransactionEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.util.RawBytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ArangodbAuditLog {
    private static final Logger logger = LoggerFactory.getLogger(ArangodbAuditLog.class);
    private final String collectionName;
    private ArangoDatabase database;
    private String userName;
    private boolean isCancelTransaction, ignoreInsert = true;
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
            logger.info(status ? "Arango Log Database (" + database.name() + ") is created." : "ERROR : To create Arango Log Database (" + database.name() + ").");
        }
    }

    public boolean isIgnoreInsert() {
        return ignoreInsert;
    }

    public void setIgnoreInsert(boolean ignoreInsert) {
        this.ignoreInsert = ignoreInsert;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void logForInsert(String name, Object data) {
        if (!ignoreInsert) {
            log("I", name, data);
        }
    }

    public void logForUpdate(String name, Object data) {
        log("U", name, data);
    }

    public void logForDelete(String name, Object data) {
        log("D", name, data);
    }

    private void log(String action, String name, Object data) {
        BaseDocument auditLog = new BaseDocument();
        auditLog.addAttribute("log_on", LocalDateTime.now());
        auditLog.addAttribute("log_by", userName);
        auditLog.addAttribute("action", action);
        auditLog.addAttribute(name, ArangodbFactory.getJsonObjectNode(data));
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

    public DocumentEntity log(BaseDocument auditLog) {
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
