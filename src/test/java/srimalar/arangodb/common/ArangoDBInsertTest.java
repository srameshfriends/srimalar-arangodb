package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.function.BiConsumer;

public class ArangoDBInsertTest {
    private static ArangodbTransaction transaction;
    private static int errorCount = 2;

    @BeforeAll
    public static void beforeAllTest() {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .host("localhost", 8529)
                .user("root")
                .password("root")
                .build();
        transaction = new ArangodbTransaction();
        transaction.setDatabase(arangoDB.db("test"));
        errorCount -= 1;
    }

    @Test
    public void insert() {
        /*System.out.println(" ----------------- INSERT CREATED ---------------- ");
        MessageProperty property = ArangoDBTest.create();
        System.out.println(property);

        MessageProperty entity = transaction.insert(property);
        System.out.println(" ----------------- INSERTED ---------------- ");
        System.out.println(entity);

        ArangoAuditLog arangoAuditLog = transaction.getAuditLogEvent();
        System.out.println(" ----------------- INSERT AUDIT ---------------- ");
        arangoAuditLog.getInsertMap().forEach(new BiConsumer<String, LinkedList<Object>>() {
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
