package io.crowdsignal.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Jimmy Spivey
 */
@Entity
public class Country {

    @Id
    private String iso;
    private String name;
    private String nicename;
    private String iso3;
    private Integer numcode;
    private Integer phonecode;

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public Integer getNumcode() {
        return numcode;
    }

    public void setNumcode(Integer numcode) {
        this.numcode = numcode;
    }

    public Integer getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(Integer phonecode) {
        this.phonecode = phonecode;
    }

}
