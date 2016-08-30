package io.crowdsignal.entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by jspivey on 7/23/15.
 */
@Entity
public class TweetEntity extends AuditColumnEntity{

    @Id
    private Long id;
    private String text;
    private String username;
    private Date tweeted;
    private Double latitude;
    private Double longitude;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Date getTweeted() {
        return this.tweeted;
    }
    public void setTweeted(Date tweeted) {
        this.tweeted = tweeted;
    }
    public Double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
