package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ArangoDBUpdateAttributeTest {
    private static ArangodbExecutor transaction;

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
    }

    @AfterAll
    public static void afterAllTest() {
        System.out.println("Commit Stream Transaction");
        transaction.commitStreamTransaction();
    }

    @Test
    public void updateTest() {
        System.out.println(" ----------------- UPDATE ---------------- ");
        Map<String, Object> param = new HashMap<>();
        param.put("locale", "IPO");
        param.put("value", "Field level update implement.");

        transaction.update(SampleModelADB.class, "2152486", param);
    }
}
