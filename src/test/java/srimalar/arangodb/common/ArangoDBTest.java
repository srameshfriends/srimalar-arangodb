package srimalar.arangodb.common;

import java.util.UUID;

public class ArangoDBTest {

    public static SampleModelADB create() {
        SampleModelADB pro = new SampleModelADB();
        String value = UUID.randomUUID().toString();
        pro.setLocale(value.substring(0, 2));
        pro.setName(value.substring(4, 8));
        pro.setValue(value);
        return pro;
    }

    public static SampleModelADB update(String key, String rev) {
        SampleModelADB pro = new SampleModelADB();
        String value = UUID.randomUUID().toString();
        pro.setKey(key);
        pro.setRev(rev);
        pro.setLocale(value.substring(0, 2));
        pro.setName(value.substring(4, 8));
        pro.setValue(value);
        return pro;
    }

    public static SampleModelADB getInstance(String key, String rev) {
        SampleModelADB pro = new SampleModelADB();
        pro.setKey(key);
        pro.setRev(rev);
        return pro;
    }
}
