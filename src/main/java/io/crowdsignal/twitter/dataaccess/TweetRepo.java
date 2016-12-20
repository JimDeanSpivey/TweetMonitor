package io.crowdsignal.twitter.dataaccess;

import io.crowdsignal.entities.TweetEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jimmy Spivey
 */
public interface TweetRepo extends CrudRepository<TweetEntity, Long> {

}
