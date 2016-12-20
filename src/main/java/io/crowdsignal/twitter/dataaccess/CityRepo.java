package io.crowdsignal.twitter.dataaccess;

import io.crowdsignal.entities.City;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Jimmy Spivey
 */
public interface CityRepo extends CrudRepository<City, Integer> {

    List<City> findByNodes_name(String name);

}
