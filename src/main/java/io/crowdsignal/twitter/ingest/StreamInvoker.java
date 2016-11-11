package io.crowdsignal.twitter.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;

import java.util.Collection;

/**
 * Created by jspivey on 7/22/15.
 */
@Component
public class StreamInvoker {

    private static final Logger log = LoggerFactory.getLogger(StreamInvoker.class);

    private SearchContextProvider searchContextProvider;
    private TwitterStream twitterStream;

    public StreamInvoker(SearchContextProvider searchContextProvider, TwitterStream twitterStream) {
        this.searchContextProvider = searchContextProvider;
        this.twitterStream = twitterStream;
    }

    public void run() {
        Collection<String> keywords = searchContextProvider.allKeywords();
        log.info("Tracking words: {}", String.join(",", keywords));
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.language("en");
        filterQuery.track(keywords.toArray(new String[keywords.size()])); //TODO: consider not tracking retweets, if this is possible for streaming api
        twitterStream.filter(filterQuery);
        log.debug("Stream invoked");
    }
}
