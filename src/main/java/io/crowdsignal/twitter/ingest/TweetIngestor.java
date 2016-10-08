package io.crowdsignal.twitter.ingest;

import io.crowdsignal.twitter.SkipTweetPredicate;
import io.crowdsignal.twitter.SpamFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

/**
 * @author Jimmy Spivey
 */
@Component
public class TweetIngestor {

    private static final Logger log = LoggerFactory.getLogger(TweetIngestor.class);

    private StreamInvoker streamInvoker;
    private SkipTweetPredicate skipTweetPredicate;
    private SpamFilter spamFilter;
    private TwitterStream twitterStream;

    public TweetIngestor(StreamInvoker streamInvoker, SkipTweetPredicate skipTweetPredicate, SpamFilter spamFilter, TwitterStream twitterStream) {
        this.streamInvoker = streamInvoker;
        this.skipTweetPredicate = skipTweetPredicate;
        this.spamFilter = spamFilter;
        this.twitterStream = twitterStream;
    }

    public void run() {
        log.trace("run()");
        ConnectableFlux<Status> tweets = Flux.<Status>create(emitter -> {
            log.trace("create()");
            final StatusListener listener = new TweetListener() {
                @Override
                public void onStatus(Status status) {
                    log.trace("next()");
                    emitter.next(status);
                }

                @Override
                public void onException(Exception ex) {
                    log.trace("error()");
                    emitter.error(ex);
                }
            };
            log.trace("streamInvoker.run()");
            twitterStream.addListener(listener);
            streamInvoker.run();
        }).publish();

        tweets
                .filter(Status::isRetweet)
                .subscribe(status -> log.info("Retweet: {}", status.getText()));
        tweets
                .filter(skipTweetPredicate)
                .publishOn(Schedulers.elastic())
                .filter(spamFilter)
                .publishOn(Schedulers.parallel())
//                .buffer(100)
                .subscribe(status -> log.info(status.getText()));

        tweets.connect();



                //.publishOn(Schedulers.elastic())
                //TODO: Save to postgresql


                //.buffer(100) TODO: see if this is useful for batching persistence of tweets
                //.doOnSubscribe(s -> streamInvoker.run())
                //.subscribeOn(Schedulers.immediate())
                //.subscribe(status -> log.info(status.getText()));
//                .subscribe();
    }

}
