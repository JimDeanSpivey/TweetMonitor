package io.crowdsignal.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.function.Predicate;

/**
 * @author Jimmy Spivey
 */
@Component
public class SkipTweetPredicate implements Predicate<Status> {

    private static final Logger log = LoggerFactory.getLogger(SkipTweetPredicate.class);

    @Override
    public boolean test(Status tweet) {
        if (tweet.isRetweet()) { //TODO: should be able to filter these out in the filter params ?
            log.trace("Is retweet, filtering out.");
            return false; //TODO: handle retweets later. Just for now, I don't want to store them in the main count
        }
        if (tweet.getUserMentionEntities() != null && tweet.getUserMentionEntities().length != 0) {
            log.trace("Contains user mention, filtering out.");
            return false; //Discard any tweets with user mentions
        }
        return true;
    }
}
