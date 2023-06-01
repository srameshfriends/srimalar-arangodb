package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArangoDBDeleteTest {
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
    public void delete() {
        /*MessageProperty property = ArangoDBTest.getInstance("2043862", null);
        System.out.println(property);

        MessageProperty entity = transaction.delete(property);
        System.out.println(" ----------------- DELETED ---------------- ");
        System.out.println(entity);

        ArangoAuditLog arangoAuditLog = transaction.getAuditLogEvent();
        System.out.println(" ----------------- DELETE AUDIT ---------------- ");
        arangoAuditLog.getDeleteMap().forEach(new BiConsumer<String, LinkedList<Object>>() {
            @Override
            public void accept(String s, LinkedList<Object> objects) {
                System.out.println(objects.get(0));
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
