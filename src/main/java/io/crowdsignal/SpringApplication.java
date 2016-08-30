package io.crowdsignal;

import io.crowdsignal.twitter.scan.SearchContextProvider;
import io.crowdsignal.twitter.scan.TweetScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "io.crowdsignal")
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
public class SpringApplication implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(TweetScanner.class);

    @Autowired
    private SearchContextProvider searchContextProvider;

    @Autowired
    private TweetScanner tweetScanner;

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.trace("SpringApplication.run");
        searchContextProvider.init();
        tweetScanner.run();
    }
}
