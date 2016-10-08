package io.crowdsignal.twitter;

import com.lambdaworks.redis.LettuceFutures;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author Jimmy Spivey
 */
@Component
public class SpamFilter implements Predicate<Status> {

    private static final Logger log = LoggerFactory.getLogger(SpamFilter.class);
    public static final String REDIS_KEY = "crowdsig:twitter:existing";

    private RedisAsyncCommands<String, String> asyncRedis;

    public SpamFilter(RedisAsyncCommands<String, String> asyncRedis) {
        this.asyncRedis = asyncRedis;
    }

    @Override
    public boolean test(Status tweet) {
        String tweetHash = tweet.getText().hashCode()+"";
        String userId = tweet.getUser().getId()+"";
        // Query synchronously to see the hashed value of the string is in redis
        // TODO: consider this: http://redis.io/topics/memory-optimization
        RedisFuture<Long> tweetAddCount = asyncRedis.sadd(
                String.format("%s:tweets", REDIS_KEY), tweetHash);
        RedisFuture<Long> userAddCount = asyncRedis.sadd(
                String.format("%s:userids", REDIS_KEY), userId);
//        asyncRedis.zadd
        asyncRedis.flushCommands();
        LettuceFutures.awaitAll(5, TimeUnit.SECONDS, tweetAddCount, userAddCount);
        // TODO: consider adding spam users and tweets to postgresql
        try {
            if (userAddCount.get() == 0) {
                log.info("Found spam user: {}", tweet.getUser().getName());
            }
            if (userAddCount.get() == 0) {
                log.info("Found spam tweet: {}", tweet.getText());
            }
            return userAddCount.get() != 0 || tweetAddCount.get() != 0;
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
