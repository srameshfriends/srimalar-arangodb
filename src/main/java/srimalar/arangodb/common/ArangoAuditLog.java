package srimalar.arangodb.common;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ArangoAuditLog {
    private final ConcurrentHashMap<String, LinkedList<Object>> insertMap, updateMap, deleteMap;

    public ArangoAuditLog() {
        insertMap = new ConcurrentHashMap<>();
        updateMap = new ConcurrentHashMap<>();
        deleteMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, LinkedList<Object>> getInsertMap() {
        return insertMap;
    }

    public ConcurrentHashMap<String, LinkedList<Object>> getUpdateMap() {
        return updateMap;
    }

    public ConcurrentHashMap<String, LinkedList<Object>> getDeleteMap() {
        return deleteMap;
    }

    public void insert(String collection, Object object) {
        if(!insertMap.containsKey(collection)) {
            insertMap.put(collection, new LinkedList<>());
        }
        insertMap.get(collection).add(object);
    }

    public void update(String collection, Object object) {
        if(!updateMap.containsKey(collection)) {
            updateMap.put(collection, new LinkedList<>());
        }
        updateMap.get(collection).add(object);
    }

    public void delete(String collection, Object object) {
        if(!deleteMap.containsKey(collection)) {
            deleteMap.put(collection, new LinkedList<>());
        }
        deleteMap.get(collection).add(object);
    }
}
