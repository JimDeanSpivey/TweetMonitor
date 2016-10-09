package io.crowdsignal.twitter.dataaccess.redis;

import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class WordCountIncrementer {

    private static final Logger log = LoggerFactory.getLogger(
            WordCountIncrementer.class
    );

    private final static String WORD_COUNT_NAMESPACE = "wordcounts";

    private RedisAsyncCommands<String, String> redisAsyncCommands;
    private RedisKeyGenerator redisKeyGenerator;
    @Value("${io.cs.redis.wordcounts.bucketsize.minutes}")
    private long minutes;

    public WordCountIncrementer(RedisAsyncCommands<String, String> redisAsyncCommands, RedisKeyGenerator redisKeyGenerator) {
        this.redisAsyncCommands = redisAsyncCommands;
        this.redisKeyGenerator = redisKeyGenerator;
    }

    private final static DateTimeFormatter dateAsKeyDtf =
        DateTimeFormat.forPattern("yyyyMMdd:HHmm")
        .withLocale(Locale.US)
        .withZone(DateTimeZone.UTC);

    public void incrementWordCount(String context, Status tweet, String word) {
        String key = redisKeyGenerator.getTimeBucketKey(
                String.format("%s:%s", WORD_COUNT_NAMESPACE, context),
                tweet.getCreatedAt(),
                TimeUnit.MINUTES.toMillis(minutes));
        redisAsyncCommands.zincrby(key, 1, word);
    }

}
