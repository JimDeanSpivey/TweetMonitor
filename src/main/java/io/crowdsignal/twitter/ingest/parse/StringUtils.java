package io.crowdsignal.twitter.ingest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    private static Set<Character> punctuations = new HashSet<>();

    static {
        punctuations.add('(');
        punctuations.add(')');
        punctuations.add('.');
        punctuations.add(',');
        punctuations.add(':');
        punctuations.add(';');
        punctuations.add('!');
        punctuations.add('?');
        punctuations.add('#');
        punctuations.add('-');
        punctuations.add('_');
    }

    public String removeEnclosingPunctuation(String string) {
        StringBuilder sb = new StringBuilder();
        boolean stillHas = true;
        for (int i = 0; i < string.length(); i++) {
            Character c = string.charAt(i);
            if (stillHas && punctuations.contains(c)) {
                stillHas = true;
                continue;
            }
            stillHas = false;
            sb.append(c);
        }
        for (int i = sb.length()-1; i >= 0; i--) {
            Character c = sb.charAt(i);
            if (punctuations.contains(c)) {
                sb.deleteCharAt(i);
                continue;
            }
            break;
        }
        return sb.toString();
    }

    public boolean honorsUnicodeBlocks(String string, Set<Character.UnicodeBlock> blocks) {
        assert string != null;
        assert blocks != null;
        return string.chars()
                .allMatch(c -> (
                        blocks.contains(Character.UnicodeBlock.of(c)
                        ))
                );
    }

    private static final Pattern CAPTURE_MESSAGE = Pattern.compile(
    "^(?:https://t\\.co/[a-zA-Z0-9]*[ \\n]+|#[^ ]*[ \\n]+)*(.*?)(?:[\\n ]+https://t\\.co/[a-zA-Z0-9]*|[ \\n]+#[^ ]*)*?$"
    );

    public String trimUrlsAndHashTags(String tweet) {
        Matcher matcher = CAPTURE_MESSAGE.matcher(tweet);
        if (matcher.find()) {
            String group = matcher.group(1);
            log.trace("Trimming entities from tweet:");
            log.trace("{}", tweet);
            log.trace("{}", group);
            return group;
        }
        return tweet;
    }

    /**
     * Produces a new string with all the capture words separated by a single
     * space, and all twitter entities removed (Urls and hashtags).
     * @param tweet
     * @return
     */
    public String withoutEntities(String tweet) {
        List<String> tokens = Arrays.asList(tweet.split("[ \\n]+"));
        StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            if (!isEntity(token)) {
                result.append(token+ " ");
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length()-1);
        }
        return result.toString();
    }

    private static final Pattern IS_ENTITY = Pattern.compile(
            "https://t\\.co/[a-zA-Z0-9]*|[ \\n]+|#[^ ]*"
    );

    private boolean isEntity(String token) {
        Matcher matcher = IS_ENTITY.matcher(token);
        return matcher.find();
    }

}
