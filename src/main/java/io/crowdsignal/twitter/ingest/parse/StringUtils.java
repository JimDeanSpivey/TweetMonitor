package io.crowdsignal.twitter.ingest.parse;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class StringUtils {

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
            "^(?:https:\\/\\/t\\.co\\/[a-zA-Z0-9]* |#[^ ]* )*" +
                    "(.*?)" +
                    "(?: https:\\/\\/t\\.co\\/[a-zA-Z0-9]*| #[^ ]*)*?$"
    );

    public String trimUrlsAndHashTags(String tweet) {
        Matcher matcher = CAPTURE_MESSAGE.matcher(tweet);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return tweet;
    }
//
//
//
//
//        String without;
//        List<String> tokens = Arrays.asList(tweet.split(" "));
//        int start = 0;
//        for (String token : tokens) {
//            if (isEntity(token)) {
//                start += token.length() + 1;
//            } else {
//                without = tweet.substring(start, tweet.length());
//                break;
//            }
//        }

//        Set<Integer> wordPositions = new HashSet<>();
//        Set<Integer> entityPositions = new HashSet<>();
//        boolean foundWord = false;
//        boolean foundEntity = false;
//        List<String> tokens = Arrays.asList(tweet.split(" "));
//        for (
//                String token : tokens
////                int pos = 0;
////                pos < tweet.length() && pos != -1;
////                pos = tweet.indexOf(" ", pos)
//                ) {
//            if (token.startsWith("#") || TWITTER_URL.matcher(tweet).find()) {
//                if (foundWord)
//            }
//        }
//    }
//
//    private boolean isEntity(String token) {
//        token.startsWith("#") || TWITTER_URL.matcher(token).find());
//    }
}
