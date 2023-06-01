package srimalar.arangodb.common;

import java.util.UUID;

public class ArangoDBTest {

    public static MessageProperty create() {
        MessageProperty pro = new MessageProperty();
        String value = UUID.randomUUID().toString();
        pro.setLocale(value.substring(0, 2));
        pro.setName(value.substring(4, 8));
        pro.setValue(value);
        return pro;
    }

    public static MessageProperty update(String key, String rev) {
        MessageProperty pro = new MessageProperty();
        String value = UUID.randomUUID().toString();
        pro.setKey(key);
        pro.setRev(rev);
        pro.setLocale(value.substring(0, 2));
        pro.setName(value.substring(4, 8));
        pro.setValue(value);
        return pro;
    }

    public static MessageProperty getInstance(String key, String rev) {
        MessageProperty pro = new MessageProperty();
        pro.setKey(key);
        pro.setRev(rev);
        return pro;
    }
}
