package io.crowdsignal.twitter.scan.parse;

import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jspivey on 7/27/15.
 *
 * Will contain complex logic for parsing out words. Currently planed:
 * Every word simply split using space. Every pair of adjacant words.
 *
 * DONE: add support for ignoring most common english words, such as "a", "the", "I"
 * TODO: filter out unicode characters with same logic as blacklisted words
 */
@Component
public class WordParser {

    private Logger log = LoggerFactory.getLogger(WordParser.class);

    @Value("#{'${io.crowdsignal.words.mostcommon}'.split(',')}")
    private Set<String> blacklist;

    @Autowired
    private StringUtils stringUtils;

//    private static final Pattern wordPattern = Pattern.compile("[\\w/&/-_]+");
    private static final Pattern urlPattern = Pattern.compile("(https?)://[^\\s/$.?#].[^\\s]*");


    public TObjectShortMap getWordsWithCounts(List<String> tokens) {
        TObjectShortMap wordCounts = new TObjectShortHashMap<String>(50);

        //Get all words separated by spaces
        Set<String> sanitizedWords = sanitizeTokens(tokens);

        sanitizedWords.forEach(t -> {

            if (blacklist.contains(t))
                return;

            wordCounts.adjustOrPutValue(t, (short)1, (short)1);
        });

        //TODO: Get all adjacent words
//        List<String> adjacants = getAdjacents(text);
//        adjacants.forEach(t -> {
//
//            //TODO: filter out words that are adjacent
//
//        });

        return wordCounts;
    }

    private static final Set<Character.UnicodeBlock> UNICODE_BLOCKS;
    static {
        UNICODE_BLOCKS = new HashSet<>();
        UNICODE_BLOCKS.add(Character.UnicodeBlock.BASIC_LATIN); //TODO: configure, maybe locale driven config/internationalization
    }

    Set<String> sanitizeTokens(List<String> tokens) {

        Set<String> acceptable = tokens.stream()
                .filter(this::isWordAcceptable)
                .map(String::toLowerCase)
                .map(stringUtils::removeEnclosingPunctuation)
                .collect(Collectors.toSet());

        acceptable.remove(""); //Remove strings that were trimmed to empty

        log.trace("Original count {}. Sanitized count {}", tokens.size(), acceptable.size());

        return acceptable;
    }

    boolean isWordAcceptable(String word) {
        return
                stringUtils.honorsUnicodeBlocks(word, UNICODE_BLOCKS) &&
                !urlPattern.matcher(word).find();
//                wordPattern.matcher(word).find()
    }

    Pattern adjacentsPattern = Pattern.compile("([^\\s]+[ ][^\\s]+)");

    List<String> getAdjacents(String text) {
        List<String> results = new ArrayList<>();

        Matcher m = adjacentsPattern.matcher(text);

        m.find();
        results.add(m.group(1));
        int lastSize = m.group(1).split(" ")[1].length();

        while (m.find(m.end(1)-lastSize)) {
            String adjacent = m.group(1);
            lastSize = adjacent.split(" ")[1].length();

            results.add(adjacent);
        }

        return results;
    }



//    public static void main(String[] args) {
//
//        WordParser wordParser = new WordParser();
//        List<String> adjacents = wordParser.getAdjacents("★ JOB ★ #hiring #ITJob #Job #New Baltimore - Sungard GMI Consultant http://t.co/L212Okmv4w ☜ view details #jobs");
//
//        adjacents.forEach(System.out::println);
//    }

}
