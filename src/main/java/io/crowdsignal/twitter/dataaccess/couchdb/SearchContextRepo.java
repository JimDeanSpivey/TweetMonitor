package io.crowdsignal.twitter.dataaccess.couchdb;

import io.crowdsignal.entities.SearchTopic;
import org.ektorp.ViewQuery;
import org.ektorp.impl.StdCouchDbConnector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jspivey on 9/9/15.
 */
@Component
public class SearchContextRepo {

    public List<SearchTopic> findSearchTerms(ViewQuery viewQuery) {

        if (viewQuery == null)
            viewQuery = new ViewQuery();

        //client sets viewQuery parms:
        //viewQuery.descending(true).limit(50);

        viewQuery
                .designDocId("_design/City") //TODO: constant or app properties
                .viewName(viewQueryName)
                .reduce(false);

        return citiesCouchDbConnection.queryView(viewQuery, SearchTopic.class);
    }

}
