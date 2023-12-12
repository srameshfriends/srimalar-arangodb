package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArangoDBInsertTest {
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
    public void insert() {
   /*     System.out.println(" ----------------- INSERT CREATED ---------------- ");
        AuditLog auditLog = new AuditLog();
        auditLog.setCreatedOn(LocalDateTime.now());
        auditLog.setCreatedBy("Kashvika");
        auditLog.setAct("U");
        auditLog.setRef("2053492");*/
        //
        SampleModelADB property = new SampleModelADB();
        property.setLocale("en");
        property.setValue("Audit Log");
        property.setName("auditLog");/*
        property.setKey("2053492");
        property.setId("messages/2053492");
        property.setRev("_f9Ep3Ju---");*/
        //
        SampleModelADB entity = transaction.insert(property);
        System.out.println(" ----------------- INSERTED ---------------- ");
        System.out.println(entity);
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
