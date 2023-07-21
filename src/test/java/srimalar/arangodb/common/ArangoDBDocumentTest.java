package srimalar.arangodb.common;

import com.arangodb.ArangoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import srimalar.arangodb.util.AQLQueryMap;

import java.io.IOException;
import java.util.List;

public class ArangoDBDocumentTest {
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
    public void findDocument() {
        System.out.println(" ----------------- DOCUMENT ---------------- ");
        Resource resource;
        try {
            ClassPathResource classPathResource = new ClassPathResource("query/arangodb.aql");
            resource = new FileSystemResource(classPathResource.getFile());
            System.out.println(resource);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        AQLQueryMap.set(List.of(resource));
        //
        MessageProperty property = ArangoDBTest.getInstance("2152260", null);
        property = transaction.getDocument(property);
        System.out.println(property);
        ArangoQuery arangoQuery = new ArangoQuery(MessageProperty.class);
        arangoQuery.setQuery(AQLQueryMap.getQuery("FETCH_BY_KEY"));
        arangoQuery.put("@key", "2152260");
        System.out.println(arangoQuery);
      /*  MessageProperty pro = transaction.fetch(builder);
        System.out.println(pro);
        List<MessageProperty> entity = transaction.fetchAll(MessageProperty.class);
        if(entity.isEmpty()) {
            System.out.println("sssssssssssssssssssssssssssssssssssssssssssssssssssss");
        } else {
            System.out.println(entity.get(0));
        }*/

        /*String query = "FOR t IN firstCollection FILTER t.name == @name RETURN t";
        Map<String, Object> bindVars = Collections.singletonMap("name", "Homer");
        ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            System.out.println("Key: " + aDocument.getKey());
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
