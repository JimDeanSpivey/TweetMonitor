//package io.crowdsignal.twitter.dataaccess.redis;
//
//import com.lambdaworks.redis.api.async.RedisAsyncCommands;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
///**
// * @author Jimmy Spivey
// *
// * Flushes the Lettuce driver's command queue after a threshold has been met.
// */
//@Component
//public class RedisCommandThreshold {
//
//    private static final Logger log = LoggerFactory.getLogger(RedisCommandThreshold.class);
//
//    @Value("${io.crowdsignal.twitter.persistence.redis.writebuffer}")
//    private int batchSize;
//    private int count;
//
//    @Autowired
//    private RedisAsyncCommands<String, String> redisAsyncCommands;
//
//    public void increment() {
////        redisAsyncCommands.zincrby(key, 1, word);
//        if (++count >= batchSize) {
//            count = 0;
//            log.info("Increment batch threshold {} met, flushing redis commands.", batchSize);
//            redisAsyncCommands.flushCommands();
//        }
//    }
//
//}
