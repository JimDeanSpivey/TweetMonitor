package io.crowdsignal.twitter.dataaccess.redis;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Locale;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class TweetCountRepo {

    private static final Logger log = LoggerFactory.getLogger(TweetCountRepo.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static DateTimeFormatter dateAsKeyDtf =
        DateTimeFormat.forPattern("yyyyMMdd:HHmm")
        .withLocale(Locale.US)
        .withZone(DateTimeZone.UTC);

    public void incrementWordCount(String context, Status tweet, String word, Integer count) {

        String key = getKey(context, tweet);
        log.trace("Persisting key:{}, word:{}, count:{}", key, word, count);
        stringRedisTemplate.opsForZSet().incrementScore(key, word, count);
    }

    private String getKey(String context, Status tweet) {
        DateTime time = new DateTime(tweet.getCreatedAt());

//        String location = "baltimore"; //TODO: need some way of parsing out the city/location names. Then persist into each city bucket
        //TODO: wrong design, need to pre-search for city name in scanner, and pass all city/locations in

//        if (tweet.getCoordinates() != null) {
//            //TODO: need a way to lookup the coordinates back to the city/location
//            //TODO: will geo override or should it create a seperate (duplicate) entry into its own bucket?
//            location = "geo_baltimore";
//        }

        int m = time.getMinuteOfHour();
        time = time.withMinuteOfHour(m - m % 5); //TODO: make the 5 configurable
        return context+":"+dateAsKeyDtf.print(time);
    }

}
