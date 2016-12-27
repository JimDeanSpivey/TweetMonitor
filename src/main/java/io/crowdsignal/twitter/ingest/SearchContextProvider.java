package io.crowdsignal.twitter.ingest;

import io.crowdsignal.entities.City;
import io.crowdsignal.twitter.dataaccess.CityRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jspivey on 9/10/15.
 */
@Component
public class SearchContextProvider {

    private Logger log = LoggerFactory.getLogger(SearchContextProvider.class);

    @Autowired
    private CityRepo cityRepo;

    @Value("${io.crowdsignal.node.name}")
    private String nodeName;

    private Collection<City> cities;
    private Collection<String> flattenedKeywords;
    private Map<String,String> keywordsToCity = new HashMap<>();

    public String keywordToCity(String keyword) {
        return keywordsToCity.get(keyword);
    }

    public Collection<String> allKeywords() {
        return flattenedKeywords;
    }

    @Transactional
    public void init() { //TODO: should be able to use lazy loading, this isn't couchdb anymore
        lookupSearchTerms();
        flattenAndIndexTerms();
    }

    private void lookupSearchTerms() {
        log.debug("Looking up cities for {}", nodeName);
        cities = cityRepo.findByNodes_name(nodeName);
        log.debug("Found {} cities", cities.size());
    }

    private void flattenAndIndexTerms() {
        ArrayList<String> flattened = cities.stream()
                .flatMap(extractTerms())
                .collect(Collectors.toCollection(ArrayList::new));
        flattenedKeywords = Collections.unmodifiableCollection(flattened);
    }

    private Function<City, Stream<? extends String>> extractTerms() {
        return e -> {
            Set<String> terms = new HashSet<>();
            // Add state/country code variations
            if (!e.getRequireCountry() && !e.getRequireState()) {
                terms.add(e.getName());
                keywordsToCity.put(e.getName(), e.getName());
            } else if (e.getRequireCountry()) {
                internalAdd(terms, e.getName(), e.getCountry().getNicename());
            } else if (e.getRequireState()) {
                internalAdd(terms, e.getName(), e.getState().getName());
            }
            // Add aliases
            //terms.add(e.getName());
        //            if (e.getAliases() != null)
        //                aliases.addAll(getAliasesAsStrings(e.getAliases()));
            // Return flattened stream (all names + aliases as a single stream)
            return terms.stream();
        };
    }

    private void internalAdd(Set<String> terms, String city, String region) {
        String withRegion = String.format("%s %s", city, region);
        terms.add(withRegion);
        keywordsToCity.put(withRegion, city);
    }

//    private Set<String> getAliasesAsStrings(Set<KeywordAlias> aliases) {
//            return aliases.stream()
//                .map(Object::toString)
//                .collect(Collectors.toSet());
//    }

}
