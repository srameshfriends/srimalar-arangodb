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
        arangodbAuditLog.setLogDatabase(arangoDB.db("test_al"));
        //
        transaction.setDatabase(arangoDB.db("test_ma"), arangodbAuditLog);
        errorCount -= 1;
    }

    @Test
    public void reload() {
        /*List<MessageProperty> newList = new ArrayList<>();
        newList.add(create());
        newList.add(create2());
        newList.add(create3());
        List<MessageProperty> oldList = transaction.fetchAll(MessageProperty.class);
        Map<String, MessageProperty> oldMap = new HashMap<>();
        oldList.forEach(prop -> oldMap.put(prop.getLocale() + prop.getName(), prop));
        List<MessageProperty> updateList = new ArrayList<>();
        List<MessageProperty> insertList = new ArrayList<>();
        Set<String> itemSet = new HashSet<>();
        newList.forEach(newProp -> {
            String name = newProp.getLocale() + newProp.getName();
            if (oldMap.containsKey(name)) {
                MessageProperty oldPro = oldMap.get(newProp.getLocale() + newProp.getName());
                if (!oldPro.getValue().equals(newProp.getValue())) {
                    oldPro.setValue(newProp.getValue());
                    if (itemSet.contains(name)) {
                        System.out.println("ERROR ---------------------------------------");
                    }
                    itemSet.add(name);
                    updateList.add(oldPro);
                }
            } else {
                insertList.add(newProp);
            }
        });
        transaction.updateAll(updateList);
        transaction.insertAll(insertList);
        System.out.println("Updated " + updateList.size());
        System.out.println("Inserted " + insertList.size());*/
        errorCount -= 1;
    }

    private MessageProperty create() {
        MessageProperty pro = new MessageProperty();
        pro.setId("messages/2164455");
        pro.setRev("_gAMlrRi---");
        pro.setKey("2164455");
        pro.setLocale("en");
        pro.setName("description");
        pro.setValue("Description");
        return pro;
    }

    private MessageProperty create2() {
        MessageProperty pro = new MessageProperty();
        pro.setLocale("en");
        pro.setName("This.is.ramesh");
        pro.setValue("This is Ramesh");
        return pro;
    }

    private MessageProperty create3() {
        MessageProperty pro = new MessageProperty();
        pro.setId("messages/2164443");
        pro.setRev("_gAMlZ02---");
        pro.setKey("2164443");
        pro.setLocale("en");
        pro.setName("amount");
        pro.setValue("Total Amount");
        return pro;
    }

    @AfterAll
    public static void afterAllTest() {
        if (errorCount == 0) {
            System.out.println("Before ------------------- commitStreamTransaction");
            transaction.commitStreamTransaction();

        } else {
            System.out.println("Before ------------------- abortStreamTransaction");
            transaction.abortStreamTransaction();
        }
    }
}
