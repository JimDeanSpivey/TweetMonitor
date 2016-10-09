package io.crowdsignal.twitter;

import com.lambdaworks.redis.LettuceFutures;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.crowdsignal.twitter.dataaccess.redis.RedisKeyGenerator;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import twitter4j.Status;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Jimmy Spivey
 */
@Component
public class SpamFilter implements Function<List<Status>, Publisher<Status>> {

    private static final Logger log = LoggerFactory.getLogger(SpamFilter.class);
    public static final String KEY_NAMESPACE = "crowdsig:twitter:existing";

    private RedisAsyncCommands<String, String> redis;
    private RedisKeyGenerator redisKeyGenerator;

    @Value("${io.cs.redis.spamfilter.bucketsize.minutes}")
    private int minutes;
    @Value("${io.cs.redis.expected.tweets.persecond}")
    private int tweetsPerSecond;

    public SpamFilter(RedisAsyncCommands<String, String> redis, RedisKeyGenerator redisKeyGenerator) {
        this.redis = redis;
        this.redisKeyGenerator = redisKeyGenerator;
    }

    @Override
    public Publisher<Status> apply(List<Status> tweets) {
        Map<Long, RedisFuture<Boolean>> tweetLookups = tweets.stream().collect(Collectors.toMap(
                t -> t.getId(),
                t -> {
                    int hashCode = t.getText().hashCode();
                    return redis.hset(getKey("tweets", hashCode), hashCode+"", "1");
                })
        );
        Map<Long, RedisFuture<Boolean>> userLookups = tweets.stream().collect(Collectors.toMap(
                t -> t.getUser().getId(),
                t -> {
                    long id = t.getUser().getId();
                    return redis.hset(getKey("userids", id), id + "", "1");
                })
        );
        redis.flushCommands();
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS,
                tweetLookups.values().toArray(new Future[0]));
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS,
                userLookups.values().toArray(new Future[0]));
        // Filter out spam tweet from collection
        List<Status> notSpam = tweets.stream().filter(tweet -> {
            try {
                Boolean newTweet = tweetLookups.get(tweet.getId()).get();
                Boolean newUser = userLookups.get(tweet.getUser().getId()).get();
                if (!newTweet) {
                    log.debug("Spam Tweet: {}", tweet.getId());
                }
                if (!newUser) {
                    log.debug("Spam User: {}", tweet.getUser().getName());
                }
                return newTweet && newUser;
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());
        log.debug("{} tweets out of {} were not spam", notSpam.size(), tweets.size());
        return Flux.fromIterable(notSpam);
    }

    private String getKey(String postfix, long value) {
        String namespace = getKeyNamespace(postfix);
        String base = redisKeyGenerator.getTimeBucketKey(
                namespace, new Date(), TimeUnit.MINUTES.toMillis(minutes)
        );
        // 60 seconds * bucketsize(in minutes) * tweetsPerSecond / 100 (redis hash size)
        return String.format("%s:%d", base, value % (60 * minutes * tweetsPerSecond / 100));
    }

    private String getKeyNamespace(String postfix) {
        return String.format("%s:%s", KEY_NAMESPACE, postfix);
    }

}
