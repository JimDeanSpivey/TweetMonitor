package io.crowdsignal.twitter;

import gnu.trove.map.TObjectShortMap;
import io.crowdsignal.twitter.dataaccess.redis.WordCountIncrementer;
import io.crowdsignal.twitter.ingest.SearchContextProvider;
import io.crowdsignal.twitter.ingest.parse.WordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Jimmy Spivey
 */
@Component
public class WordCounter implements Consumer<Status> {

    private static final Logger log = LoggerFactory.getLogger(WordCounter.class);

    private WordParser wordParser;
    private WordCountIncrementer countRepo;
    private SearchContextProvider searchContextProvider;

    public WordCounter(WordParser wordParser, WordCountIncrementer countRepo, SearchContextProvider searchContextProvider) {
        this.wordParser = wordParser;
        this.countRepo = countRepo;
        this.searchContextProvider = searchContextProvider;
    }

    @Override
    public void accept(Status tweet) {
        //TODO: seems like the raw text needs to be HTML escaped
        String text = tweet.getText();
        List<String> tokens = Arrays.asList(text.split("[ \\n]+"));
        // Extract cities (contexts)
        //TODO: need to recreate how twitter matches terms like Washington. DC
        Set<String> citiesFound = searchContextProvider.allKeywords().stream()
                .filter(tweet.getText()::contains)
                .collect(Collectors.toSet());
        citiesFound.forEach(c -> persistWordCounts(
                tokens,
                searchContextProvider.keywordToCity(c))
        );
    }

    private void persistWordCounts(List<String> tokens, String city) {
        log.trace("Persisting word counts");
        TObjectShortMap wordsWithCounts = wordParser.getWordsWithCounts(tokens);
        wordsWithCounts.forEachEntry((k, v) -> {
                    countRepo.incrementWordCount(
                            city,
                            (String) k
                    );
                    return true;
                }
        );
    }
}
