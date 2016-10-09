package io.crowdsignal.twitter.dataaccess.redis;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Locale;

/**
 * @author Jimmy Spivey
 */
@Component
public class RedisKeyGenerator {

    private final static DateTimeFormatter dateAsKeyDtf =
            DateTimeFormat.forPattern("yyyyMMdd:HHmm")
                    .withLocale(Locale.US)
                    .withZone(DateTimeZone.UTC);

    /**
     * It's best to only use factors of max time size. EG: Factors of 24 for hours,
     * factors of 60 for minutes/seconds.
     * @param namespace
     * @param time
     * @param bucketsize
     * @return
     */
    public String getTimeBucketKey(String namespace, Date time, long bucketsize) {
        long millis = time.getTime();
        millis -= millis % bucketsize;
        return String.format("%s:%s", namespace, dateAsKeyDtf.print(millis));
    }

}
