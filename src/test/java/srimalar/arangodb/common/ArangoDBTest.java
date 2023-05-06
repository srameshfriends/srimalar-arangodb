package srimalar.arangodb.common;

import com.arangodb.entity.DocumentEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ArangoDBTest {

    public static MessageProperty create() {
        MessageProperty pro = new MessageProperty();
        String value = UUID.randomUUID().toString();
        pro.setLocale(value.substring(0, 2));
        pro.setName(value.substring(4, 8));
        pro.setValue(value);
        Number index = Math.random() * (100 - 1);
        Number serial = Math.random() * (1000 - 100);
        Number rounding = Math.random() * (10 - 2);
        pro.setIndex(index.intValue());
        pro.setSerialNo(serial.longValue());
        pro.setRounding(BigDecimal.valueOf(rounding.doubleValue()));
        pro.setActive(true);
        pro.setCreatedOn(LocalDateTime.now());
        pro.setUpdatedOn(LocalDate.now());
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
        Number index = Math.random() * (100 - 1);
        Number serial = Math.random() * (1000 - 100);
        Number rounding = Math.random() * (10 - 2);
        pro.setIndex(index.intValue());
        pro.setSerialNo(serial.longValue());
        pro.setRounding(BigDecimal.valueOf(rounding.doubleValue()));
        pro.setActive(true);
        pro.setCreatedOn(LocalDateTime.now());
        pro.setUpdatedOn(LocalDate.now());
        return pro;
    }

    public static MessageProperty getInstance(String key, String rev) {
        MessageProperty pro = new MessageProperty();
        pro.setKey(key);
        pro.setRev(rev);
        return pro;
    }
}
