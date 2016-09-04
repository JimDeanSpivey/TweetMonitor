package io.crowdsignal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Jimmy Spivey
 */
@Entity
public class KeywordAlias {

    @Id
    private Integer id;
    @Column(name="keyword_id") //Fixes some hibernate duplicate key bug. Naming strategy seems to be off
    private Integer keywordId;
    private String alias;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getKeywordId() {
        return this.keywordId;
    }
    public void setKeywordId(Integer keywordId) {
        this.keywordId = keywordId;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
