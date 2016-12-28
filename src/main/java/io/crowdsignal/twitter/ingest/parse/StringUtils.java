package io.crowdsignal.twitter.ingest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
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

    private static final Pattern HASHTAG_SPACE = Pattern.compile(
            "#[^\\s\\n]*?[\\s\\n]"
    );
    private static final Pattern SPACE_HASHTAG = Pattern.compile(
            "[\\s\\n]#[^\\s\\n]*"
    );
    private static final Pattern TW_URL_SPACE = Pattern.compile(
            "https?://t.co/[\\w\\d]{10}[\\s\\n]"
    );
    private static final Pattern SPACE_TW_URL = Pattern.compile(
            "[\\s\\n]https?://t.co/[\\w\\d]{10}"
    );
    private static final Pattern SPECIAL_CHARS = Pattern.compile(
            "[^\\p{L}\\d\\s\\n]"
    );

    /**
     * Produces a new string with all the capture words separated by a single
     * space, and all twitter entities removed (Urls and hashtags).
     * @param tweet
     * @return
     */
    public String withoutEntities(final String tweet) {
        String result = tweet;
        result = HASHTAG_SPACE.matcher(result).replaceAll("");
        result = SPACE_HASHTAG.matcher(result).replaceAll("");
        result = TW_URL_SPACE.matcher(result).replaceAll("");
        result = SPACE_TW_URL.matcher(result).replaceAll("");
        return result;
    }

    /**
     * Replaces all special characters with white space. Works on unicode
     * characters as well.
     * eg: blah..blah becomes: blahblah
     * @param str
     * @return
     */
    public String withoutSpecials(final String str) {
        return SPECIAL_CHARS.matcher(str).replaceAll("");
    }

}
