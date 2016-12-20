package io.crowdsignal.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Jimmy Spivey
 */
@Entity
public class TwitterApiNode extends AuditColumnEntity {

    @Id
    private Integer id;
    private String name;
    @OneToOne
    @JoinColumn(name = "oath_tokens_id")
    private TwitterApiToken token;
    @ManyToMany
    @JoinTable(
        name="city_twitter_api_node",
        joinColumns = @JoinColumn(name="api_node_id"),
        inverseJoinColumns = @JoinColumn(name="city_id")
    )
    private Set<City> cities;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<City> getCities() {
        return this.cities;
    }
    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
    public TwitterApiToken getToken() {
        return this.token;
    }
    public void setToken(TwitterApiToken token) {
        this.token = token;
    }
}
