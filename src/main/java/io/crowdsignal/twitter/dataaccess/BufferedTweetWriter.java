package io.crowdsignal.twitter.dataaccess;

import io.crowdsignal.entities.TweetEntity;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jspivey on 9/6/15.
 */
@Component
public class BufferedTweetWriter {

    private static final Logger log = LoggerFactory.getLogger(BufferedTweetWriter.class);
//    private static final String INSERT_STATEMENT = new StringBuilder()
//        .append("INSERT_STATEMENT INTO tweets (id, data) ")
//        .append("VALUES (?, ?) ON CONFLICT DO UPDATE ")
//        .append("SET data = tweets.")
//        .toString();

    @Autowired
    private TweetRepository tweetRepository;
    @Value("${io.crowdsignal.twitter.streaming.writebuffer}")
    private Integer limit;

    private List<TweetEntity> buffer = new ArrayList<>();

//    @Async
    //TODO: If this is not thread safe with regards to 'buffer' ?
    public void saveTweet(TweetEntity tweet) {
        buffer.add(tweet);

        if (buffer.size() >= limit) {
            log.debug("Saving {} tweets", buffer.size());
            // Shallow copy to prevent concurrent modification
            // --Because the buffer may accept new entries in another thread?
            List<TweetEntity> copied = new ArrayList<>(buffer);

            tweetRepository.save(copied);
            buffer.clear();
//            jdbcTemplate.batchUpdate(INSERT_STATEMENT, new BatchPreparedStatementSetter() {
//                @Override
//                public void setValues(PreparedStatement ps, int i) throws SQLException {
//                    TweetEntity tweet = copied.get(i);
//                    ps.setLong(1, tweet.getId());
//                    try {
//                        ps.setString(2,
//                            new ObjectMapper().writeValueAsString(tweet)
//                        );
//                    } catch (IOException e) {
//                        log.error("Unable to write Tweet as JSON", e);
//                    }
//                }
//
//                @Override
//                public int getBatchSize() {
//                    return copied.size();
//                }
//            });
        }
    }

}
