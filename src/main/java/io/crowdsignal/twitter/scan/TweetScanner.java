package io.crowdsignal.twitter.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;

import java.util.Collection;

/**
 * Created by jspivey on 7/22/15.
 */
@Component
public class TweetScanner {

    private static final Logger log = LoggerFactory.getLogger(TweetScanner.class);

    @Autowired
    private TweetStreamListener tweetStreamListener;

    @Autowired
    private SearchContextProvider searchContextProvider;

    @Autowired
    private TwitterStream twitterStream;

    public void run() {
        log.debug("Running TweetScanner");
        Collection<String> keywords = searchContextProvider.allKeywords();
        StringBuilder formatted = new StringBuilder();
        keywords.stream().forEach(formatted::append);
        log.info("Tracking words: {}", formatted);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.language("en");
        filterQuery.track(keywords.toArray(new String[keywords.size()]));
        twitterStream.filter(filterQuery);
        log.debug("Stream invoked");


        // examine redis here for absense of any new keys, and call twitterStream.filter again if it went silent (no rows)
        // TODO: should this be done in a new class. Thread chaperoning is a separate concern.
    }
}
