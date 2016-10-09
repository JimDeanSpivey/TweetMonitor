package io.crowdsignal.twitter.dataaccess.redis;

import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jimmy Spivey
 */
public class RedisKeyGeneratorTest {

    public static final String NAMESPACE = "test";

    @Test
    public void testGetTimeBucketKey() throws Exception {
        Date now = new Date();
        String utcFormatted = ZonedDateTime.now(ZoneOffset.UTC).withSecond(0).withNano(0).toString();
        System.out.println(utcFormatted);

        RedisKeyGenerator generator = new RedisKeyGenerator();
        String key = generator.getTimeBucketKey(
                NAMESPACE, now, TimeUnit.MINUTES.toMillis(5)
        );
        System.out.println(key);
        key = generator.getTimeBucketKey(
                NAMESPACE, now, TimeUnit.HOURS.toMillis(6)
        );
        System.out.println(key);
        key = generator.getTimeBucketKey(
                NAMESPACE, now, TimeUnit.HOURS.toMillis(24)
        );
        System.out.println(key);
        key = generator.getTimeBucketKey(
                NAMESPACE, now, TimeUnit.HOURS.toMillis(48)
        );
        System.out.println(key);
    }

}
