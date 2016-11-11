package io.crowdsignal.twitter.ingest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.crowdsignal.entities.Keyword;
import io.crowdsignal.entities.KeywordAlias;
import io.crowdsignal.twitter.dataaccess.KeywordRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jspivey on 9/10/15.
 */
public class SearchContextProvider {

    private Logger log = LoggerFactory.getLogger(SearchContextProvider.class);

    private KeywordRepo keywordRepo;

    @Value("${io.crowdsignal.node.name}")
    private String nodeName;

    private Collection<Keyword> keywords;
    private Collection<String> flattenedKeywords;
    private Multimap<String,String> keywordsToAliases;

    public SearchContextProvider(KeywordRepo keywordRepo) {
        this.keywordRepo = keywordRepo;
    }

    public Collection<String> searchTermToName(String searchTerm) {
        return keywordsToAliases.get(searchTerm);
    }

    public Collection<String> allKeywords() {
        return flattenedKeywords;
    }

    @Transactional
    public void init() {
        lookupSearchTerms();
        flattenAndIndexTerms();
    }

    private void lookupSearchTerms() {
        keywords = keywordRepo.findByNodes_name(nodeName);
    }

    private void flattenAndIndexTerms() {
        Multimap<String,String> indexed = ArrayListMultimap.create();
        ArrayList<String> flattened = keywords.stream().flatMap(e -> {
            Set<String> aliases = new HashSet<>();
            aliases.add(e.getName());
            if (e.getAliases() != null)
                aliases.addAll(getAliasesAsStrings(e.getAliases()));
            // Also build map of aliases
            aliases.forEach(a -> indexed.put(a, e.getName()));
            // Return flattened stream (all names + aliases as a single stream)
            return aliases.stream();
        }).collect(Collectors.toCollection(ArrayList::new));

        flattenedKeywords = Collections.unmodifiableCollection(flattened);
        keywordsToAliases = ImmutableMultimap.copyOf(indexed);
    }

    private Set<String> getAliasesAsStrings(Set<KeywordAlias> aliases) {
            return aliases.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

}
