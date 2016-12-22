package io.crowdsignal.config;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import io.crowdsignal.config.dataaccess.TwitterApiNodeRepo;
import io.crowdsignal.entities.TwitterApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.transaction.Transactional;

/**
 * Created by jspivey on 7/22/15.
 */
@Configuration
public class ResourceBeans {

    // redis
    @Bean
    public RedisClient redisClient(
        @Value("${redis.hostname}") String hostname,
        @Value("${redis.port}") String port,
        @Value("${redis.password}") String password
    ) {
        RedisClient client = RedisClient.create(
            String.format("redis://%s@%s:%s", password, hostname, port)
        );
        return client;
    }

    @Bean
    public RedisAsyncCommands<String, String> redisAsyncCommands(RedisClient client) {
        RedisAsyncCommands<String, String> async = client.connect().async();
        async.setAutoFlushCommands(false);
        return async;
    }

    @Bean
    public RedisCommands<String, String> redisCommands(RedisClient client) {
        RedisCommands<String, String> sync = client.connect().sync();
        return sync;
    }

    @Bean
    @Transactional
    public TwitterStream twitterStream(
        @Value("${io.crowdsignal.node.name}") String node,
        TwitterApiNodeRepo twitterApiNodeRepo
    ) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        TwitterStream stream = new TwitterStreamFactory(cb.build()).getInstance();
        TwitterApiToken token = twitterApiNodeRepo.findByName(node).getToken();

        stream.setOAuthConsumer(token.getAppId(), token.getAppSecret());
        stream.setOAuthAccessToken(
            new AccessToken(token.getAccessToken(), token.getAccessTokenSecret())
        );
        //stream.addListener(streamListener);
        return stream;
    }

}
