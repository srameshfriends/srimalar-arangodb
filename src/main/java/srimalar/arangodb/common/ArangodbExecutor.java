package srimalar.arangodb.common;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.*;
import com.arangodb.model.*;
import com.arangodb.util.RawBytes;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import srimalar.arangodb.util.CommonConstant;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Currently supported only single database transaction management
 * The arangodb collection automatically added in to the transaction.
 * Perform arangodb database operation it will begin transaction automatically (beginStreamTransaction).
 * The transaction life cycle manually need to be call (commitStreamTransaction) or (abortStreamTransaction)
 * Make sure the operation will be completed otherwise it will cause unknown issues of your database transactions.
 * Continue improvements required for performance check.
 *
 * @author Ramesh S
 * @since 1.0
 */
@Slf4j
@Component
@Scope(value = CommonConstant.SCOPE_REQUEST)
public class ArangodbExecutor {
    private final ConcurrentHashSet<StreamTransactionSet> transactionSet;
    private ArangoDatabase database;
    private boolean isCancelTransaction;

    private ArangodbAuditLog auditLog;

    public ArangodbExecutor() {
        transactionSet = new ConcurrentHashSet<>();
    }

    public void setDatabase(ArangoDatabase database, ArangodbAuditLog auditLog) {
        this.database = database;
        if (!database.exists()) {
            boolean status = database.create();
            log.info(status ? "Arango Database (" + database.name() + ") is created." : "ERROR : To create Arango Database (" + database.name() + ").");
        }
        this.auditLog = auditLog;
    }

    public ArangodbAuditLog getArangodbAuditLog() {
        return auditLog;
    }

    public ArangoCollection getCollection(String name) {
        ArangoCollection collection = database.collection(name);
        if (!collection.exists()) {
            throw new IllegalArgumentException("ERROR: Arangodb Collection not found (" + name + ")");
        }
        return collection;
    }

    public ArangoCollection getOrCreateCollection(String name) {
        ArangoCollection collection = database.collection(name);
        if (!collection.exists()) {
            collection.create();
        }
        return collection;
    }

    public void setUniqueIndex(Class<?> clazz, String... fields) {
        String name = ArangodbFactory.getCollectionName(clazz);
        ArangoCollection collection = getOrCreateCollection(name);
        collection.ensurePersistentIndex(Arrays.asList(fields), new PersistentIndexOptions().unique(true));
    }

    public void dropCollection(String name) {
        ArangoCollection collection = database.collection(name);
        if (collection.exists()) {
            collection.drop();
        }
    }

    private String beginStreamTransaction(String collection) {
        Optional<StreamTransactionSet> optional = transactionSet.stream()
                .filter(streamTransactionSet -> streamTransactionSet.getCollectionSet().contains(collection)).findFirst();
        if (optional.isEmpty()) {
            StreamTransactionSet streamTransaction = new StreamTransactionSet(collection);
            StreamTransactionEntity entity = database.beginStreamTransaction(streamTransaction.getStreamTransactionOptions());
            streamTransaction.setTransactionEntity(entity);
            transactionSet.add(streamTransaction);
            optional = Optional.of(streamTransaction);
        }
        return optional.get().getId();
    }

    public void abortStreamTransaction() {
        transactionSet.forEach(stream -> database.abortStreamTransaction(stream.getId()));
        auditLog.abortStreamTransaction();
    }

    public void commitStreamTransaction() {
        if(isCancelTransaction) {
            abortStreamTransaction();
            auditLog.abortStreamTransaction();
        } else {
            transactionSet.forEach(stream -> database.commitStreamTransaction(stream.getId()));
            auditLog.commitStreamTransaction();
        }
    }

    public void cancelStreamTransaction() {
        this.isCancelTransaction = true;
        auditLog.cancelStreamTransaction();
    }

    public String getDatabaseName() {
        return database.name();
    }

    public <T> T insert(Object object) {
        if (object == null) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(object.getClass());
        ArangoCollection collection = getOrCreateCollection(name);
        DocumentCreateOptions options = new DocumentCreateOptions().streamTransactionId(beginStreamTransaction(name));
        RawBytes rawBytes = ArangodbFactory.getRawBytes(object);
        DocumentEntity entity = collection.insertDocument(rawBytes, options, RawBytes.class);
        T result = ArangodbFactory.getNewInstance(entity, object);
        auditLog.logForInsert(name, ArangodbFactory.getJsonObjectNode(result));
        return result;
    }

    public <T> List<T> insertAll(List<?> objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return null;
        }
        Object first = objectList.get(0);
        String name = ArangodbFactory.getCollectionName(first.getClass());
        ArangoCollection collection = getOrCreateCollection(name);
        DocumentCreateOptions createOptions = new DocumentCreateOptions().streamTransactionId(beginStreamTransaction(name));
        List<T> resultList = new ArrayList<>();
        objectList.forEach((Consumer<Object>) object -> {
            RawBytes rawBytes = ArangodbFactory.getRawBytes(object);
            DocumentEntity entity = collection.insertDocument(rawBytes, createOptions, RawBytes.class);
            T result = ArangodbFactory.getNewInstance(entity, object);
            auditLog.logForInsert(name, result);
            resultList.add(result);
        });
        return resultList;
    }

    private DocumentUpdateOptions createUpdateOptions(String name) {
        DocumentUpdateOptions options = new DocumentUpdateOptions().streamTransactionId(beginStreamTransaction(name));
        options.returnOld(true);
        options.returnNew(true);
        options.ignoreRevs(false);
        return options;
    }

    public <T> T update(Object object) {
        if (!(object instanceof EntityIdentity entityId)) {
            throw new IllegalArgumentException("ERROR: Document entity not valid to update (" + object + ")");
        }
        Class<?> tClass = object.getClass();
        String name = ArangodbFactory.getCollectionName(tClass);
        ArangoCollection collection = getCollection(name);
        RawBytes rawBytes = ArangodbFactory.getRawBytes(object);
        DocumentUpdateEntity<RawBytes> entity = collection.updateDocument(entityId.getKey(), rawBytes, createUpdateOptions(name));
        auditLog.logForUpdate(name, ArangodbFactory.getObject(entity.getOld(), tClass));
        return ArangodbFactory.getObject(entity.getNew(), tClass);
    }

    public <T> T update(Class<?> tClass, String key, Map<String, Object> valueMap) {
        if (tClass == null) {
            throw new IllegalArgumentException("ERROR: Arangodb executor - update document class invalid.");
        }
        if (key == null || key.isBlank() || valueMap == null || valueMap.isEmpty()) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(tClass);
        StringBuilder builder = new StringBuilder();
        builder.append("FOR x IN ").append(name).append(" FILTER x._key == '").append(key).append("' ");
        String selectQry = builder + " RETURN x";
        Object old = fetch(tClass, selectQry, null);
        if (old == null) {
            return null;
        }
        builder.append(" UPDATE x WITH { ");
        StringBuilder fb = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            fb.append(entry.getKey()).append(" : ").append("@v").append(entry.getKey()).append(",");
            param.put("v" + entry.getKey(), entry.getValue());
        }
        builder.append(fb.substring(0, fb.length() - 1)).append(" } IN ").append(name);
        database.query(builder.toString(), tClass, param);
        Object newRecord = fetch(tClass, selectQry, null);
        //
        RawBytes oldRawBytes = ArangodbFactory.getRawBytes(old);
        auditLog.logForUpdate(name, ArangodbFactory.getObject(oldRawBytes, tClass));
        RawBytes newRawBytes = ArangodbFactory.getRawBytes(newRecord);
        return ArangodbFactory.getObject(newRawBytes, tClass);
    }

    public <T> List<T> updateAll(List<?> objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return null;
        }
        Object object = objectList.get(0);
        if(!(object instanceof EntityIdentity)) {
            throw new IllegalArgumentException("Arangodb update entity not supported.");
        }
        Class<?> tClass = object.getClass();
        String name = ArangodbFactory.getCollectionName(tClass);
        ArangoCollection collection = getCollection(name);
        DocumentUpdateOptions updateOptions = createUpdateOptions(name);
        List<T> resultList = new ArrayList<>();
        objectList.forEach((Consumer<Object>) obj -> {
            EntityIdentity adbEntity = (EntityIdentity)obj;
            RawBytes rawBytes = ArangodbFactory.getRawBytes(object);
            DocumentUpdateEntity<RawBytes> entity = collection.updateDocument(adbEntity.getKey(), rawBytes, updateOptions);
            auditLog.logForUpdate(name, ArangodbFactory.getObject(entity.getOld(), tClass));
            resultList.add(ArangodbFactory.getObject(entity.getNew(), tClass));
        });
        return resultList;
    }

    private DocumentDeleteOptions createDeleteOptions(String name) {
        DocumentDeleteOptions options = new DocumentDeleteOptions().streamTransactionId(beginStreamTransaction(name));
        options.returnOld(true);
        return options;
    }

    @SuppressWarnings("unchecked")
    public <T> T delete(Object object) {
        if (!(object instanceof EntityIdentity baseEntity)) {
            throw new IllegalArgumentException("ERROR: Document entity not valid to delete (" + object + ")");
        }
        Class<?> tClass = object.getClass();
        String name = ArangodbFactory.getCollectionName(tClass);
        ArangoCollection collection = getCollection(name);
        DocumentDeleteEntity<RawBytes> entity = collection.deleteDocument(
                baseEntity.getKey(), createDeleteOptions(name), RawBytes.class);
        Object old = ArangodbFactory.getObject(entity.getOld(), tClass);
        auditLog.logForDelete(name, old);
        return (T)old;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> deleteAll(List<?> objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return null;
        }
        Object object = objectList.get(0);
        if(!(object instanceof EntityIdentity)) {
            throw new IllegalArgumentException("Arangodb update entity not supported.");
        }
        Class<?> tClass = object.getClass();
        String name = ArangodbFactory.getCollectionName(tClass);
        ArangoCollection collection = getCollection(name);
        DocumentDeleteOptions deleteOpn = createDeleteOptions(name);
        List<T> resultList = new ArrayList<>();
        objectList.forEach((Consumer<Object>) obj -> {
            EntityIdentity adbEntity = (EntityIdentity) obj;
            DocumentDeleteEntity<RawBytes> entity = collection.deleteDocument(adbEntity.getKey(), deleteOpn, RawBytes.class);
            Object old = ArangodbFactory.getObject(entity.getOld(), tClass);
            auditLog.logForDelete(name, old);
            resultList.add((T) old);
        });
        return resultList;
    }

    public <T> T findFirst(Class<?> cType) {
        String name = ArangodbFactory.getCollectionName(cType);
        return fetch(cType, "FOR x IN " + name + " SORT x.key LIMIT 1 RETURN x", null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDocument(Object object) {
        if (!(object instanceof EntityIdentity baseEntity)) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(object.getClass());
        ArangoCollection collection = getCollection(name);
        BaseDocument document = collection.getDocument(baseEntity.getKey(), BaseDocument.class);
        return (T) ArangodbFactory.getTypeObject(document, object.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T getDocument(Object object, boolean isForTransaction) {
        if(!isForTransaction) {
            return getDocument(object);
        }
        if (!(object instanceof EntityIdentity baseEntity)) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(object.getClass());
        ArangoCollection collection = getCollection(name);
        DocumentReadOptions readOption = new DocumentReadOptions().streamTransactionId(beginStreamTransaction(name));
        BaseDocument document = collection.getDocument(baseEntity.getKey(), BaseDocument.class, readOption);
        return (T) ArangodbFactory.getTypeObject(document, object.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T getDocument(DocumentEntity entity, Class<?> type) {
        if(entity == null) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(type);
        ArangoCollection collection = getCollection(name);
        BaseDocument document = collection.getDocument(entity.getKey(), BaseDocument.class);
        return (T) ArangodbFactory.getTypeObject(document, type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDocument(DocumentEntity entity, Class<?> type, boolean isForTransaction) {
        if(!isForTransaction) {
            return getDocument(entity, type);
        }
        if (entity == null) {
            return null;
        }
        String name = ArangodbFactory.getCollectionName(type);
        ArangoCollection collection = getCollection(name);
        BaseDocument document = collection.getDocument(entity.getKey(), BaseDocument.class);
        return (T) ArangodbFactory.getTypeObject(document, type);
    }

    public <T> ArangoCursor<T> query(String query, Class<?> type, Map<String, Object> parameters) {
        return query(query, type, parameters);
    }

    public <T> ArangoCursor<T> query(String query, Class<?> type) {
        return query(type, query, null);
    }

    @SuppressWarnings("unchecked")
    public <T> ArangoCursor<T> query(Class<?> type, String query, Map<String, Object> parameters) {
        return (ArangoCursor<T>) database.query(query, type, parameters);
    }

    public <T> T fetch(ArangoQuery builder) {
        return fetch(builder.getType(), builder.toString(), builder.getParameters());
    }

    @SuppressWarnings("unchecked")
    public <T> T fetch(Class<?> type, String query, Map<String, Object> parameters) {
        Object result;
        try (ArangoCursor<T> cursor = query(type, query, parameters)) {
            Optional<?> optional = cursor.stream().findFirst();
            result = optional.orElse(null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return (T) result;
    }

    public <T> List<T> fetchAll(ArangoQuery builder) {
        return fetchAll(builder.getType(), builder.toString(), builder.getParameters());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> fetchAll(Class<?> type, String query, Map<String, Object> parameters) {
        List<T> resultList;
        try (ArangoCursor<MessageProperty> cursor = query(type, query, parameters)) {
            resultList = (List<T>) cursor.stream().collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return resultList;
    }

    public <T> List<T> fetchAll(Class<?> type) {
        return fetchAll(new ArangoQuery(type));
    }

    static class StreamTransactionSet {
        private final StreamTransactionOptions streamTransactionOptions;
        private final Set<String> collectionSet;
        private StreamTransactionEntity transactionEntity;

        StreamTransactionSet(String... collections) {
            streamTransactionOptions = new StreamTransactionOptions().writeCollections(collections);
            Set<String> set = new HashSet<>();
            Collections.addAll(set, collections);
            this.collectionSet = Collections.unmodifiableSet(set);
        }

        public StreamTransactionOptions getStreamTransactionOptions() {
            return streamTransactionOptions;
        }

        public Set<String> getCollectionSet() {
            return collectionSet;
        }

        public void setTransactionEntity(StreamTransactionEntity transactionEntity) {
            this.transactionEntity = transactionEntity;
        }

        public String getId() {
            return transactionEntity.getId();
        }
    }
}