package io.crowdsignal.config;

import io.crowdsignal.twitter.scan.TweetStreamListener;
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
    //TODO: Manage these in twitter_api_node table
    public TwitterStream twitterStream(
            @Value("${io.crowdsignal.twitter.oath.appId}") String appId,
            @Value("${io.crowdsignal.twitter.oath.appSecret}") String appSecret,
            @Value("${io.crowdsignal.twitter.oath.accessToken}") String accessToken,
            @Value("${io.crowdsignal.twitter.oath.accessTokenSecret}") String accessTokenSecret,
            TweetStreamListener streamListener
    ) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        TwitterStream stream = new TwitterStreamFactory(cb.build()).getInstance();
        stream.setOAuthConsumer(appId, appSecret);
        stream.setOAuthAccessToken(
            new AccessToken(accessToken, accessTokenSecret)
        );
        stream.addListener(streamListener);
        return stream;
    }

}
