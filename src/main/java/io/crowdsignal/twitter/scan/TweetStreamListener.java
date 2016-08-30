package io.crowdsignal.twitter.scan;

import gnu.trove.map.TObjectShortMap;
import io.crowdsignal.entities.TweetEntity;
import io.crowdsignal.twitter.dataaccess.BufferedTweetWriter;
import io.crowdsignal.twitter.dataaccess.redis.TweetCountRepo;
import io.crowdsignal.twitter.scan.parse.WordParser;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Created by jspivey on 9/9/15.
 */
@Component
public class TweetStreamListener implements StatusListener {

    private static final Logger log = LoggerFactory.getLogger(TweetStreamListener.class);

    @Autowired
    private SearchContextProvider searchContextProvider;

    @Autowired
    private WordParser wordParser;

    @Autowired
    private TweetCountRepo countRepo;

    @Autowired
    private BufferedTweetWriter bufferedTweetWriter;

    private boolean skipTweet(Status tweet) {

        if (tweet.isRetweet()) { //TODO: should be able to filter these out in the filter params ?
            log.trace("Is retweet, filtering out.");
            return true; //TODO: handle retweets later. Just for now, I don't want to store them in the main count
        }

        if (tweet.getUserMentionEntities() != null && tweet.getUserMentionEntities().length != 0) {
            log.trace("Contains user mention, filtering out.");
            return true; //Discard any tweets with user mentions
        }

        return false;
    }

    private TweetEntity toTweetEntity(Status tweet) {
        TweetEntity entity = new TweetEntity();
        entity.setId(tweet.getId());
        entity.setText(tweet.getText());
        entity.setUsername(tweet.getUser().getScreenName());
        entity.setTweeted(tweet.getCreatedAt());
        if (tweet.getGeoLocation() != null) {
            entity.setLatitude(tweet.getGeoLocation().getLatitude());
            entity.setLongitude(tweet.getGeoLocation().getLongitude());
        }
        return entity;
    }

    @Override
    public void onStatus(Status tweet) {
        log.debug(tweet.getText());
        if (skipTweet(tweet))
            return;

        //TODO: seems like the raw text needs to be HTML escaped
        String text = tweet.getText();
        List<String> tokens = Arrays.asList(text.split(" "));
        // Extract cities (contexts)
        Set<String> citiesFound = searchContextProvider.searchTerms().stream()
                .filter(tweet.getText()::contains)
                .collect(Collectors.toSet());
        citiesFound.forEach(c -> persistWordCounts(tokens, c, tweet));

        bufferedTweetWriter.saveTweet(
            toTweetEntity(tweet)
        );
    }

    private void persistWordCounts(List<String> tokens, String city, Status tweet) {
        // Persist word counts to redis
        TObjectShortMap wordsWithCounts = wordParser.getWordsWithCounts(tokens);

        wordsWithCounts.forEachEntry((k, v) -> {
                    countRepo.incrementWordCount(
                            city,
                            tweet,
                            (String) k,
                            (int) wordsWithCounts.get(k)
                    );
                    return true;
                }
        );
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        log.info("onDeletionNotice");
        //TODO
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedTweets) {
        // https://dev.twitter.com/streaming/overview/messages-types#limit_notices
        //TODO: definitely want to track this somewhere
        log.warn("Stream Limit notice: {}", numberOfLimitedTweets);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        log.info("onScrubGeo");
        //TODO
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
    public void onException(Exception ex) {
        log.error("onException", ex);
    }
}
