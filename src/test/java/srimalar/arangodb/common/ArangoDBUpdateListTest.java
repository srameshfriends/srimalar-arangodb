package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArangoDBUpdateListTest {
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
        ArangodbAuditLog arangodbAuditLog = new ArangodbAuditLog();
        arangodbAuditLog.setLogDatabase(arangoDB.db("test_aa"));
        //
        transaction.setDatabase(arangoDB.db("test_mm"), arangodbAuditLog);
        errorCount -= 1;
    }

    @Test
    public void updateList() {
        System.out.println(" ----------------- INSERT LIST ---------------- ");
       /* List<MessageProperty> proList = new ArrayList<>();
        MessageProperty pro1 = ArangoDBTest.update("2049831", null);
        proList.add(pro1);
        MessageProperty pro2 = ArangoDBTest.update("2049551", null);
        proList.add(pro2);
        List<MessageProperty>  result = transaction.updateAll(proList);
        result.forEach(new Consumer<MessageProperty>() {
            @Override
            public void accept(MessageProperty mes) {
                System.out.println(mes);
            }
        });
        //
        System.out.println(" ----- AUDIT INSERTED ----- ");
        transaction.getAuditLogEvent().getUpdateMap().forEach(new BiConsumer<String, LinkedList<Object>>() {
            @Override
            public void accept(String s, LinkedList<Object> objects) {
                objects.forEach(otr -> System.out.println(otr));
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
