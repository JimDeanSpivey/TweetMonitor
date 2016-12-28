package io.crowdsignal.twitter.ingest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Produces a new string with all the capture words separated by a single
     * space, and all twitter entities removed (Urls and hashtags).
     * @param tweet
     * @return
     */
    public String withoutEntities(String tweet) {
        String result;
        result = tweet.replaceAll("#[^\\s\\n]*?[\\s\\n]", "");
        result = result.replaceAll("[\\s\\n]#[^\\s\\n]*", "");
        result = result.replaceAll("https?://t.co/[\\w\\d]{10}[\\s\\n]", "");
        result = result.replaceAll("[\\s\\n]https?://t.co/[\\w\\d]{10}", "");
        return result;
    }

    /**
     * Replaces all special characters with white space. Works on unicode
     * characters as well.
     * eg: blah..blah becomes: blah  blah
     * @param str
     * @return
     */
    public String whiteOutSpecials(String str) {
        return str.replaceAll("[^\\p{L}\\d\\s\\n]", " ");
    }

    private static final Pattern IS_ENTITY = Pattern.compile(
            "(?i)https://t\\.co/[\\w\\d]*|[ \\n]+|#[^ ]*"
    );

    private boolean isEntity(String token) {
        Matcher matcher = IS_ENTITY.matcher(token);
        return matcher.find();
    }

}
