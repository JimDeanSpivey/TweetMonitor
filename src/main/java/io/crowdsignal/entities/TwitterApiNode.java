package io.crowdsignal.entities;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

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
        name="keyword_twitter_api_node",
        joinColumns = @JoinColumn(name="api_node_id"),
        inverseJoinColumns = @JoinColumn(name="keyword_id")
    )
    private Set<Keyword> keywords;

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
    public Set<Keyword> getKeywords() {
        return this.keywords;
    }
    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }
    public TwitterApiToken getToken() {
        return this.token;
    }
    public void setToken(TwitterApiToken token) {
        this.token = token;
    }
}
