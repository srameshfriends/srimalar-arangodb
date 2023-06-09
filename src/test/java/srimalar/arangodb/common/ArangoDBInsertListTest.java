package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArangoDBInsertListTest {
    private static ArangodbExecutor transaction;
    private static int errorCount = 2;

    @BeforeAll
    public static void beforeAllTest() {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .host("localhost", 8529)
                .user("root")
                .password("root")
                .build();
        transaction = new ArangodbExecutor();
        ArangodbAuditLog auditLog = new ArangodbAuditLog();
        auditLog.setLogDatabase(arangoDB.db("test_aa"));
        transaction.setDatabase(arangoDB.db("test_mm"), auditLog);
        errorCount -= 1;
    }

    @Test
    public void insertsGet() {
        /*List<MessageProperty> proList = new ArrayList<>();
        proList.add(ArangoDBTest.create());
        proList.add(ArangoDBTest.create());
        proList.add(ArangoDBTest.create());
        System.out.println(" ----------------- BEFORE INSERT LIST ---------------- ");
        proList.forEach(System.out::println);
        List<MessageProperty> entityList = transaction.insertAll(proList);
        System.out.println(" ----------------- INSERT LIST ---------------- ");
        entityList.forEach(System.out::println);
        System.out.println(" ----------------- AUDIT INSERT LIST ---------------- ");
        transaction.getAuditLogEvent().getInsertMap().forEach(new BiConsumer<String, LinkedList<Object>>() {
            @Override
            public void accept(String s, LinkedList<Object> objects) {
                objects.forEach(System.out::println);
            }
        });*/
        errorCount -= 1;
    }

    @AfterAll
    public static void afterAllTest() {
        if(errorCount == 0) {
            System.out.println("Before ------------------- commitStreamTransaction");
            transaction.commitStreamTransaction();

        } else {
            System.out.println("Before ------------------- abortStreamTransaction");
            transaction.abortStreamTransaction();
        }
    }
}
