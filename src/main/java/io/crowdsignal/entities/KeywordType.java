package io.crowdsignal.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Jimmy Spivey
 */
@Entity
public class KeywordType {

    @Id
    private String code;
    private String description;

    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
