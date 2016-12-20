package io.crowdsignal.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Jimmy Spivey
 */
@Entity
public class State {

    @Id
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
