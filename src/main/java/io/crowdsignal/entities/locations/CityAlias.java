package io.crowdsignal.entities.locations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Jimmy Spivey
 */
@Entity
public class CityAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

}
