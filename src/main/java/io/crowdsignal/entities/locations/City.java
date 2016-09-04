package io.crowdsignal.entities.locations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jspivey on 9/7/15.
 */
@Entity
@Table(name="city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String search_name;
    // Altername names loaded from cities5000.txt, lots verbosity and translations that aren't that helpful
    //private List<String> alternate_names;
    // Better/manually curated search aliases, eg: for "New York City": NYC, New York,
    private Float latitude;
    private Float longitude;
    private String country_name;
    private String country_code;
    private String state_name;
    private String state_code;
    private Integer population;
    private String timezone;
    private Boolean require_state; //State code or full state name
    private Boolean require_country; //full country name

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getRequire_state() {
        return require_state;
    }

    public void setRequire_state(Boolean require_state) {
        this.require_state = require_state;
    }

    public Boolean getRequire_country() {
        return require_country;
    }

    public void setRequire_country(Boolean require_country) {
        this.require_country = require_country;
    }
}
