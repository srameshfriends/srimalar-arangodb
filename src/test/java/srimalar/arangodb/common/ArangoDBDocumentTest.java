package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.function.BiConsumer;

public class ArangoDBDocumentTest {
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
        /*System.out.println(" ----------------- DOCUMENT ---------------- ");
        MessageProperty property = ArangoDBTest.getInstance("2043863", null);
        MessageProperty entity = transaction.getDocument(property);
        System.out.println(entity);*/
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
