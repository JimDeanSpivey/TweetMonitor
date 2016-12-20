package io.crowdsignal.config.dataaccess;

import io.crowdsignal.entities.TwitterApiNode;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jimmy Spivey
 */
public interface TwitterApiNodeRepo extends CrudRepository<TwitterApiNode, Integer> {

    TwitterApiNode findByName(String name);
}
