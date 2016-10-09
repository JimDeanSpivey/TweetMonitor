package io.crowdsignal.twitter.dataaccess;

import io.crowdsignal.entities.TweetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Jimmy Spivey
 */
@Component
public class TweetWritingSubscriber implements Consumer<List<Status>> {

    private static final Logger log = LoggerFactory.getLogger(TweetWritingSubscriber.class);

    private TweetRepo tweetRepo;

    public TweetWritingSubscriber(TweetRepo tweetRepo) {
        this.tweetRepo = tweetRepo;
    }

    @Override
    public void accept(List<Status> statuses) {
        log.debug("Saving {} tweets", statuses.size());
        tweetRepo.save(
                statuses.stream().map(this::toTweetEntity).collect(Collectors.toList())
        );
    }

    private TweetEntity toTweetEntity(Status tweet) {
        TweetEntity entity = new TweetEntity();
        entity.setId(tweet.getId());
        entity.setText(tweet.getText());
        entity.setUsername(tweet.getUser().getScreenName());
        entity.setTweeted(tweet.getCreatedAt());
        if (tweet.getGeoLocation() != null) {
            entity.setLatitude(tweet.getGeoLocation().getLatitude());
            entity.setLongitude(tweet.getGeoLocation().getLongitude());
        }
        return entity;
    }
}
