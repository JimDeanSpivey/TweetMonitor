package io.crowdsignal.twitter.scan;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.crowdsignal.entities.SearchTopic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jspivey on 9/10/15.
 */
@Component
public class SearchContextProvider {

    private Logger log = LoggerFactory.getLogger(SearchContextProvider.class);

    @Autowired
    private SearchContextRepo searchContextRepo;

    private Collection<SearchTopic> searchTerms;
    private Collection<String> flattenedSearchTerms;
    private Map<String,String> searchTermsToNames;

    public String searchTermToName(String searchTerm) {
        return searchTermsToNames.get(searchTerm);
    }


    public Collection<String> searchTerms() {
        return flattenedSearchTerms;
    }

    public Collection<SearchTopic> getSearchTerms() {
        return searchTerms;
    }

    public void init() {
        lookupSearchTerms();
        flattenAndIndexTerms();
    }

    private void lookupSearchTerms() {
        // Query from couchdb
        List<SearchTopic> searchTerms = searchContextRepo.findSearchTerms(null);

        // Additional search terms defined in ExtraCities.json
        //TODO: implement query with postgres
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            List<SearchContextEntity> fromFile = mapper.readValue(
//                    getClass().getResourceAsStream("/io/crowdsignal/config/ExtraCities.json"),
//                    new TypeReference<List<SearchContextEntity>>() {
//                    });
//            searchTerms.addAll(fromFile);
//        } catch (DbAccessException e) {
//            log.warn("Most likely a read timeout due to running the first time. The view generation " +
//                    "has not completed yet. Will simply retry the query again. If this message keeps" +
//                    " looping something obviously went wrong.", e);
//            lookupSearchTerms();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        this.searchTerms = Collections.unmodifiableCollection(searchTerms);
    }

    // This code is confusing..
    private void flattenAndIndexTerms() {
        Multimap<String,String> indexed = ArrayListMultimap.create();
        ArrayList<String> flattened = searchTerms.stream().flatMap(e -> {
            List<String> aliases = new ArrayList<>();
            aliases.add(e.getName());
            if (e.getSearch_aliases() != null)
                aliases.addAll(e.getSearch_aliases());
            // Build Alias -> Name index
            aliases.forEach(a -> indexed.put(a, e.getName()));
            // Return flattened stream (all names + aliases as a single stream)
            return aliases.stream();
        }).collect(Collectors.toCollection(ArrayList::new));

        flattenedSearchTerms = Collections.unmodifiableCollection(flattened);
        searchTermsToNames = Collections.unmodifiableMap(indexed);
    }

}
