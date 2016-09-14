package io.crowdsignal.twitter.dataaccess;

import io.crowdsignal.entities.Keyword;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jimmy Spivey
 */
public interface KeywordRepo extends CrudRepository<Keyword, Integer> {

    List<Keyword> findByNodes_name(String name);

}
