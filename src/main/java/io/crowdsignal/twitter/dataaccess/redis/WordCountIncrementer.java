package io.crowdsignal.twitter.dataaccess.redis;

import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class WordCountIncrementer {

    private static final Logger log = LoggerFactory.getLogger(WordCountIncrementer.class);

    @Autowired
    private ZincrbyBatchedDao dao;

    private final static DateTimeFormatter dateAsKeyDtf =
        DateTimeFormat.forPattern("yyyyMMdd:HHmm")
        .withLocale(Locale.US)
        .withZone(DateTimeZone.UTC);

    public void incrementWordCount(String context, Status tweet, String word) {
        String key = getKey(context, tweet);
        dao.increment(key, word);
        //TODO: Invoke RedisCommandWriter here
        //stringRedisTemplate.opsForZSet().incrementScore(key, word, 1);
    }

    private String getKey(String context, Status tweet) {
        DateTime time = new DateTime(tweet.getCreatedAt());
        int m = time.getMinuteOfHour();
        time = time.withMinuteOfHour(m - m % 5); //TODO: make the 5 configurable
        return context+":"+dateAsKeyDtf.print(time);
    }

}
