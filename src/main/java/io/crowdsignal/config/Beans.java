package io.crowdsignal.config;

import io.crowdsignal.twitter.scan.TweetStreamListener;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setUsePool(true);
        return jedisConnectionFactory;
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
    @Primary
    @ConfigurationProperties(prefix="io.crowdsignal.db.postgres.datasource")
    public DataSource postgresDatasource() {
        return DataSourceBuilder.create().build();
    }

//    @Bean
//    public DataSource dataSource(
//        @Value("${io.crowdsignal.db.url}") String url,
//        @Value("${io.crowdsignal.db.driver}") String driver,
//        @Value("${io.crowdsignal.db.username}") String username,
//        @Value("${io.crowdsignal.db.password}") String password
//    ) {
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setUrl(url);
//        ds.setDriverClassName(driver);
//        ds.setUsername(username);
//        ds.setPassword(password);
//        return ds;
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
//    @Bean
//    public PlatformTransactionManager txManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }

    @Bean
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
