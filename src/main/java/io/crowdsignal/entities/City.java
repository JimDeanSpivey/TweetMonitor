package io.crowdsignal.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Jimmy Spivey
 */
@Entity
public class City extends AuditColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String nameSpecialChars;
    private Double latitude;
    private Double longitude;
    @OneToOne
    @JoinColumn(name="country_code", referencedColumnName="iso")
    private Country country;
    @OneToOne
    @JoinColumn(name="state_code", referencedColumnName="code")
    private State state;
    private Boolean requireCountry;
    private Boolean requireState;
    private Integer population;
    @ManyToMany
    @JoinTable(
        name = "city_twitter_api_node",
        joinColumns = @JoinColumn(name = "city_id"),
        inverseJoinColumns = @JoinColumn(name = "api_node_id")
    )
    private Set<TwitterApiNode> nodes;

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

    public String getNameSpecialChars() {
        return nameSpecialChars;
    }

    public void setNameSpecialChars(String nameSpecialChars) {
        this.nameSpecialChars = nameSpecialChars;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Boolean getRequireCountry() {
        return requireCountry;
    }

    public void setRequireCountry(Boolean requireCountry) {
        this.requireCountry = requireCountry;
    }

    public Boolean getRequireState() {
        return requireState;
    }

    public void setRequireState(Boolean requireState) {
        this.requireState = requireState;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Set<TwitterApiNode> getNodes() {
        return nodes;
    }

    public void setNodes(Set<TwitterApiNode> nodes) {
        this.nodes = nodes;
    }
}
