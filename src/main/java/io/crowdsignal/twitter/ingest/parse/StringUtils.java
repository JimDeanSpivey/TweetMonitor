package io.crowdsignal.twitter.scan.parse;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jspivey on 7/27/15.
 */
@Component
public class StringUtils {

    public String removeUnicodeBlocks(String string, Set<Character.UnicodeBlock> blocks) {
        assert string != null;
        assert blocks != null;

        StringBuilder sb = new StringBuilder(string);

        string.chars().forEach(c -> {
            if (!blocks.contains(Character.UnicodeBlock.of(c)))
                sb.append(c);
        });

        return sb.toString();
    }

    public boolean honorsUnicodeBlocks(String string, Set<Character.UnicodeBlock> blocks) {
        assert string != null;
        assert blocks != null;

        return string.chars()
                .allMatch(c -> (
                        blocks.contains(Character.UnicodeBlock.of(c)
                        )));
    }


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
        punctuations.add('#'); //Added for twitter. TODO: This is really a twitter focused method not a string util.
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
            Character c = string.charAt(i);
            if (punctuations.contains(c)) {
                sb.deleteCharAt(i);
                continue;
            }
            break;
        }

        return sb.toString();
    }
}
