package io.crowdsignal.twitter.ingest;

import io.crowdsignal.twitter.SpamFilter;
import io.crowdsignal.twitter.WordCounter;
import io.crowdsignal.twitter.dataaccess.TweetWritingSubscriber;
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
    private SpamFilter spamFilter;
    private TwitterStream twitterStream;
    private WordCounter wordCounter;
    private TweetWritingSubscriber tweetWritingSubscriber;

    public TweetIngestor(StreamInvoker streamInvoker, SpamFilter spamFilter, TwitterStream twitterStream, WordCounter wordCounter, TweetWritingSubscriber tweetWritingSubscriber) {
        this.streamInvoker = streamInvoker;
        this.spamFilter = spamFilter;
        this.twitterStream = twitterStream;
        this.wordCounter = wordCounter;
        this.tweetWritingSubscriber = tweetWritingSubscriber;
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

//        tweets
//                .filter(Status::isRetweet)
//                .subscribe(status -> log.info("Retweet: {}", status.getText().hashCode()));

        ConnectableFlux<Status> spamFiltered = tweets
//                .filter(skipTweetPredicate) //I think user mentions might be okay.
                .filter(status -> !status.isRetweet())
                .buffer(3) // One problem with larger values is that more spam could get through on different nodes.
                .publishOn(Schedulers.elastic())
                .flatMap(spamFilter)
                //.doOnNext(new SpamFilter())
//                .flatMap(Flux::fromIterable)
                //.filter(spamFilter)
                .publishOn(Schedulers.parallel())
//                .buffer(100)
                //TODO: wordcounts and postgresql
//                .subscribe(status -> log.info(status.getText().hashCode()+""));
                .publish();

        // Word counts
        spamFiltered
                .subscribeOn(Schedulers.parallel())
                .subscribe(wordCounter);

        spamFiltered
                .buffer(5) //change to 100
                .subscribeOn(Schedulers.elastic())
                .subscribe(tweetWritingSubscriber);

        tweets.connect();
        spamFiltered.connect();



                //.publishOn(Schedulers.elastic())
                //TODO: Save to postgresql


                //.buffer(100) TODO: see if this is useful for batching persistence of tweets
                //.doOnSubscribe(s -> streamInvoker.run())
                //.subscribeOn(Schedulers.immediate())
                //.subscribe(status -> log.info(status.getText()));
//                .subscribe();
    }

}
