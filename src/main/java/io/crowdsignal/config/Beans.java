package io.crowdsignal.config;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulConnection;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import io.crowdsignal.config.dataaccess.TwitterApiNodeRepo;
import io.crowdsignal.entities.TwitterApiToken;
import io.crowdsignal.twitter.ingest.TweetStreamListener;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by jspivey on 7/22/15.
 */
@Configuration
public class Beans {

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
        return async;
    }

    @Bean
    public StatefulConnection<String, String> redisConnection(RedisClient client) {
        StatefulRedisConnection<String, String> connection = client.connect();
        connection.setAutoFlushCommands(false);
        return connection;
    }

    @Bean
    @Transactional
    public TwitterStream twitterStream(
        @Value("${io.crowdsignal.node.name}") String node,
        TwitterApiNodeRepo twitterApiNodeRepo,
        TweetStreamListener streamListener
    ) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        TwitterStream stream = new TwitterStreamFactory(cb.build()).getInstance();
        TwitterApiToken token = twitterApiNodeRepo.findByName(node).getToken();

        stream.setOAuthConsumer(token.getAppId(), token.getAppSecret());
        stream.setOAuthAccessToken(
            new AccessToken(token.getAccessToken(), token.getAccessTokenSecret())
        );
        stream.addListener(streamListener);
        return stream;
    }

}
