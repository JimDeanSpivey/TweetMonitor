package io.crowdsignal.entities;

import java.util.List;

/**
 * Created by jspivey on 9/8/15.
 *
 * A generic container that search topics fit into. For example, a TweetMonitor
 * might want to monitor all activity on the most populated cities. Data is
 * read from the database for city names and any configured search_aliases,
 * and is stored into this object. This object is fed to the TweetMonitor class.
 * This could also be done for other topics besides just cities, such as
 * natural disaster monitors.
 */
public class SearchTopic {

    private String name;                    // Default name
    private List<String> search_aliases;    // optional, Uses OR logic
    // TODO: should implement this sometime
//    private List<String> additional_contexts; // optional, Uses AND logic

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSearch_aliases() {
        return search_aliases;
    }

    public void setSearch_aliases(List<String> search_aliases) {
        this.search_aliases = search_aliases;
    }
}
