package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArangoDBUpdateTest {
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
    public void updateTest() {
        /*System.out.println(" ----------------- UPDATE ---------------- ");
        MessageProperty property = ArangoDBTest.getInstance("2049260", null);
        MessageProperty entity = transaction.update(property);
        System.out.println(entity);
        System.out.println(" ----------------- AUDIT ---------------- ");
        transaction.getAuditLogEvent().getUpdateMap().forEach(new BiConsumer<String, LinkedList<Object>>() {
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
