//package io.crowdsignal.twitter;
//
//import com.lambdaworks.redis.LettuceFutures;
//import com.lambdaworks.redis.RedisFuture;
//import com.lambdaworks.redis.api.async.RedisAsyncCommands;
//import io.crowdsignal.twitter.dataaccess.redis.RedisKeyGenerator;
//import org.joda.time.DateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import twitter4j.Status;
//
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Predicate;
//
///**
// * @author Jimmy Spivey
// */
//@Component
//public class SpamFilter_Old implements Predicate<List<Status>> {
//
//    private static final Logger log = LoggerFactory.getLogger(SpamFilter_Old.class);
//    public static final String KEY_NAMESPACE = "crowdsig:twitter:existing";
//
//    private RedisAsyncCommands<String, String> asyncRedis;
//    private RedisKeyGenerator redisKeyGenerator;
//
//    public SpamFilter_Old(RedisAsyncCommands<String, String> asyncRedis, RedisKeyGenerator redisKeyGenerator) {
//        this.asyncRedis = asyncRedis;
//        this.redisKeyGenerator = redisKeyGenerator;
//    }
//
//    @Override
//    public boolean test(List<Status> tweets) {
////        Map<String, String> map = tweets.stream().collect(Collectors.toMap(
////                t -> t.getText().hashCode() + "",
////                t -> t.getUser().getId() + ""
////                )
////        );
//        tweets.stream().forEach(tweet -> {
//            String tweetHash = tweet.getText().hashCode()+"";
//            String userId = tweet.getUser().getId()+"";
//            RedisFuture<Long> tweetAddCount = asyncRedis.sadd(
//                    String.format("%s:tweets", KEY_NAMESPACE), tweetHash);
//            RedisFuture<Long> userAddCount = asyncRedis.sadd(
//                    String.format("%s:userids", KEY_NAMESPACE), userId);
//        });
////        String tweetHash = tweet.getText().hashCode()+"";
////        String userId = tweet.getUser().getId()+"";
//        // Query synchronously to see the hashed value of the string is in redis
//        // TODO: consider this: http://redis.io/topics/memory-optimization
//        RedisFuture<Long> tweetAddCount = asyncRedis.sadd(
//                String.format("%s:tweets", KEY_NAMESPACE), tweetHash);
//        RedisFuture<Long> userAddCount = asyncRedis.sadd(
//                String.format("%s:userids", KEY_NAMESPACE), userId);
//        //not really possible to use hmset since there are multiple keys
////        asyncRedis.hmset(getKey(String.format("%s:tweets", KEY_NAMESPACE), ), map);
//        asyncRedis.flushCommands();
//        LettuceFutures.awaitAll(5, TimeUnit.SECONDS, tweetAddCount, userAddCount);
//        // TODO: consider adding spam users and tweets to postgresql
//        try {
//            if (userAddCount.get() == 0) {
//                log.info("Found spam user: {}", tweet.getUser().getName());
//            }
//            if (userAddCount.get() == 0) {
//                log.info("Found spam tweet: {}", tweet.getText());
//            }
//            return userAddCount.get() != 0 || tweetAddCount.get() != 0;
//        } catch (InterruptedException e) {
//            throw new IllegalStateException(e);
//        } catch (ExecutionException e) {
//            throw new IllegalStateException(e);
//        }
//    }
//
//    private String getKey(String namespace, DateTime time, int minutes, long value) {
//        String base = redisKeyGenerator.getTimeBucketKey(namespace, time, minutes);
//        return String.format("%s:%l", base, value % 720);
//    }
//}
