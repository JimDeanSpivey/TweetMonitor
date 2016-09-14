package io.crowdsignal.config;

import io.crowdsignal.config.dataaccess.TwitterApiNodeRepo;
import io.crowdsignal.entities.TwitterApiToken;
import io.crowdsignal.twitter.scan.TweetStreamListener;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public JedisConnectionFactory jedisConnectionFactory(
        @Value("${redis.hostname}") String hostname,
        @Value("${redis.port}") String port,
        @Value("${redis.password}") String password
    ) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setUsePool(true);
        factory.setHostName(hostname);
        factory.setPort(Integer.valueOf(port));
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(
        JedisConnectionFactory jedisConnectionFactory
    ) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
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
