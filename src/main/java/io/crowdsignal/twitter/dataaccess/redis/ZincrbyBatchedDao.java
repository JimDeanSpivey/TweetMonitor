package io.crowdsignal.twitter.dataaccess.redis;

import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jimmy Spivey
 */
@Component
public class ZincrbyBatchedDao {

    private static final Logger log = LoggerFactory.getLogger(ZincrbyBatchedDao.class);

    private int batchSize = 100; //TODO: configure from properties
    private int count;

    @Autowired
    private RedisAsyncCommands<String, String> redisAsyncCommands;

    public void increment(String key, String word) {
        redisAsyncCommands.zincrby(key, 1, word);
        if (++count >= batchSize) {
            count = 0;
            log.info("Increment batch threshold met, flushing redis commands.");
            redisAsyncCommands.flushCommands();
        }
    }

}
