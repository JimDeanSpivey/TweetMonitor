package io.crowdsignal.twitter.dataaccess.redis;

import com.lambdaworks.redis.ScoredValue;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.crowdsignal.twitter.ingest.WordCounts;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class WordCountIncrementer {

    private static final Logger log = LoggerFactory.getLogger(
            WordCountIncrementer.class
    );

    private final static String WORD_COUNT_NAMESPACE = "wordcounts";
    @Value("${io.cs.redis.wordcounts.bucket.size.minutes}")
    private int minutes;
    @Value("${io.cs.redis.wordcounts.bucket.mincount}")
    private int minCount;

    private RedisAsyncCommands<String, String> redisAsyncCommands;
    private RedisKeyGenerator redisKeyGenerator;
    private WordCounts wordCounts;


    public WordCountIncrementer(RedisAsyncCommands<String, String> redisAsyncCommands, RedisKeyGenerator redisKeyGenerator, WordCounts wordCounts) {
        this.redisAsyncCommands = redisAsyncCommands;
        this.redisKeyGenerator = redisKeyGenerator;
        this.wordCounts = wordCounts;
    }

    public void incrementWordCount(String context, Status tweet, String word) {
        String redisKeyPrefix = String.format("%s:%s", WORD_COUNT_NAMESPACE, context);
        String timeBucket = redisKeyGenerator.getTimeBucketKey(
                new Date(),
                TimeUnit.MINUTES.toMillis(minutes)
        );
        String redisKey = String.format("%s:%s", redisKeyPrefix, timeBucket);
         // consider have 2 variations of keys, one that is inserted into realtime and one that is filtered and batch inserted.
        wordCounts.incrementWordCount(timeBucket, redisKey, word);
    }

    @Scheduled(fixedRate = 30 * 1000)
    /**
     * Runs every 30 seconds and finds buckets that are old enough to be persisted to
     * Redis and discarded in the JVM memory. It will only persist word counts that are
     * than a <i>minCount</i>. Word counts that are too small are just discarded. This
     * memory efficient filtering before persisting to redis, so that low count words do
     * not have to be found and deleted later.
     */
    public void persistWords() {
        Date start = new Date();
        log.info("Begin Word Count persistence");
        Set<String> buckets = wordCounts.getAllBuckets().stream()
                .filter(b -> {
                    Date bucket = redisKeyGenerator.parseDateFromKey(b);
                    long elapsed = ChronoUnit.SECONDS.between(
                            bucket.toInstant(), start.toInstant()
                    );
                    int threshold = minutes * 60 + 5; // 5 minutes = threshold of 305 seconds
                    log.debug("Elapsed: {}, Threshold: {}", elapsed, threshold);
                    // If more time has elapsed beyond the buckets ability to accept new values
                    // EG: 310 elapsed seconds since bucket's date and  now > 305 threshold
                    return elapsed > threshold; //TODO: need to make sure this is flushing at the right time but also aggressive enough to persist sooner (more realtime)
                })
                .collect(Collectors.toSet()
        );
        buckets.stream()
                .forEach( b -> {
                    wordCounts.getZsets(b).forEachEntry(1, zset -> {
                        String redisKey = zset.getKey();
                        List<ScoredValue<String>> scores = new ArrayList<>();
                        zset.getValue().forEachEntry(1, e -> {
                            String word = e.getKey();
                            Integer score = e.getValue();
                            if (score >= minCount) {
                                log.debug("Adding word '{}' with {} counts to zset {}", word, score, redisKey);
                                scores.add(new ScoredValue<>(score, word));
                            }
                        });
                        if (!scores.isEmpty()) {
                            log.debug("Adding {} scores to {}", scores.size(), redisKey);
                            redisAsyncCommands.zadd(redisKey, scores.toArray(new ScoredValue[0]));
                        }
                    });
                    redisAsyncCommands.flushCommands();
                    wordCounts.removeZsets(b);
                }
        );
        Period period = new Period(
                new Date().getTime() - start.getTime()
        );
        log.info("End Word Count persistence. Running time: {}",
                ISOPeriodFormat.standard().print(period)
        );
    }


}
