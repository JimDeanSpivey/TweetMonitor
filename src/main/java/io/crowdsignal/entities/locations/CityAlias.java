package io.crowdsignal.entities.locations;

/**
 * @author Jimmy Spivey
 */
//@Entity
public class CityAlias {

    private Integer cityId;
    private String alias;

    public Integer getCityId() {
        return this.cityId;
    }
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
