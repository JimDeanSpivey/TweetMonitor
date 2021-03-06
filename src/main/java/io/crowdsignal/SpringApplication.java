package io.crowdsignal;

import io.crowdsignal.twitter.ingest.SearchContextProvider;
import io.crowdsignal.twitter.ingest.TweetIngestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SpringApplication implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(SpringApplication.class);

    @Autowired
    private SearchContextProvider searchContextProvider;

    @Autowired
    private TweetIngestor tweetIngestor;

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("SpringApplication.run");
        searchContextProvider.init(); //TODO: move this initilization to spring?
        tweetIngestor.run();
    }
}
