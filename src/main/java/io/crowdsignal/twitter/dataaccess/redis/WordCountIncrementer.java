package io.crowdsignal.twitter.dataaccess.redis;

import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Locale;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class WordCountIncrementer {

    private static final Logger log = LoggerFactory.getLogger(
            WordCountIncrementer.class
    );

    @Value("${io.crowdsignal.twitter.persistence.redis.bucketsize.minutes}")
    private Integer minutes;

    @Autowired
    private RedisCommandThreshold commandThreshold;

    @Autowired
    private RedisAsyncCommands<String, String> redisAsyncCommands;

    private final static DateTimeFormatter dateAsKeyDtf =
        DateTimeFormat.forPattern("yyyyMMdd:HHmm")
        .withLocale(Locale.US)
        .withZone(DateTimeZone.UTC);

    public void incrementWordCount(String context, Status tweet, String word) {
        String key = getKey(context, tweet);
        redisAsyncCommands.zincrby(key, 1, word);
        commandThreshold.increment();
    }

    private String getKey(String context, Status tweet) {
        DateTime time = new DateTime(tweet.getCreatedAt());
        int m = time.getMinuteOfHour();
        time = time.withMinuteOfHour(m - m % minutes);
        return context+":"+dateAsKeyDtf.print(time);
    }

}
