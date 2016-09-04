package io.crowdsignal.entities;

import javax.persistence.Entity;

/**
 * @author Jimmy Spivey
 */
@Entity
public class KeywordAlias {

    private Integer keyword_id;
    private String alias;

    public Integer getKeyword_id() {
        return this.keyword_id;
    }
    public void setKeyword_id(Integer keyword_id) {
        this.keyword_id = keyword_id;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
