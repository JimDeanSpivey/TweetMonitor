package io.crowdsignal.entities.locations;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Jimmy Spivey
 */
@Entity
@Table(name="city_alias")
public class CityAlias {


    private Integer cityId;
    private String alias;



}
