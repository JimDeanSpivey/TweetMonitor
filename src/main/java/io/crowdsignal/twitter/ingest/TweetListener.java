package io.crowdsignal.twitter.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * @author Jimmy Spivey
 */
public abstract class TweetListener implements StatusListener {

    private static final Logger log = LoggerFactory.getLogger(TweetListener.class);

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedTweets) {
        // https://dev.twitter.com/streaming/overview/messages-types#limit_notices
        //TODO: definitely want to track this somewhere
        //      --Would be a good metric to see over-used and under-used words
        //      --so that they can be better reconfigured.
        log.warn("Stream Track Limit notice: {}", numberOfLimitedTweets);
    }

    @Override
    public void onStallWarning(StallWarning warningEvent) {
        //TODO: will this happen to me? What rate should I be reading at?
        log.warn("Stream Warning. [Code: {}] [Message: {}] [Percent: {}]",
                warningEvent.getCode(),
                warningEvent.getMessage(),
                warningEvent.getPercentFull());
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

}
