package io.crowdsignal.twitter.ingest;

import io.crowdsignal.twitter.SpamFilter;
import io.crowdsignal.twitter.WordCounter;
import io.crowdsignal.twitter.dataaccess.TweetWritingSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${io.cs.postgresql.tweets.writebuffer}")
    private int tweetWriteSize;
    @Value("${io.cs.redis.spamfilter.querybuffer}")
    private int spamQuerySize;

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
        ConnectableFlux<Status> tweets = Flux.<Status>create(emitter -> {
            final StatusListener listener = new TweetListener() {
                @Override
                public void onStatus(Status status) {
                    emitter.next(status);
                }
                @Override
                public void onException(Exception ex) {
                    emitter.error(ex);
                }
            };
            twitterStream.addListener(listener);
            streamInvoker.run();
        }).publish();

//        tweets
//                .filter(Status::isRetweet)
//                .subscribe(status -> log.info("Retweet: {}", status.getText().hashCode()));

        ConnectableFlux<Status> spamFiltered = tweets
//                .filter(skipTweetPredicate) //I think user mentions might be okay.
                .filter(status -> !status.isRetweet())
                .buffer(1) // One problem with larger values is that more spam could get through on different nodes.
                .publishOn(Schedulers.elastic())
                .flatMap(spamFilter)
                .publishOn(Schedulers.parallel())
                .publish();

        spamFiltered
                .publishOn(Schedulers.parallel())
                .subscribe(wordCounter);

        spamFiltered
                .buffer(tweetWriteSize)
                .publishOn(Schedulers.elastic())
                .subscribe(tweetWritingSubscriber);

        tweets.connect();
        spamFiltered.connect();
    }

}
