package io.crowdsignal.entities;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * @author Jimmy Spivey
 */
@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    @OneToOne
    @JoinColumn(name = "type", referencedColumnName = "code")
    private KeywordType keywordType;
    @OneToMany//(mappedBy = "keyword")
    @JoinColumn(name = "keyword_id", referencedColumnName = "id")
    private Set<KeywordAlias> aliases;
    @ManyToMany
    @JoinTable(
        name = "keyword_twitter_api_node",
        joinColumns = @JoinColumn(name = "keyword_id"),
        inverseJoinColumns = @JoinColumn(name = "api_node_id")
    )
    private Set<TwitterApiNode> nodes;

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
    public KeywordType getKeywordType() {
        return this.keywordType;
    }
    public void setKeywordType(KeywordType keywordType) {
        this.keywordType = keywordType;
    }
    public Set<KeywordAlias> getAliases() {
        return this.aliases;
    }
    public void setAliases(Set<KeywordAlias> aliases) {
        this.aliases = aliases;
    }

}
