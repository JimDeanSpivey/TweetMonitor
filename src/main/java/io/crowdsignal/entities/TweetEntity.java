package io.crowdsignal.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jspivey on 7/23/15.
 */
@Entity
@Table(name="tweet")
public class TweetEntity {

    @Id
    private Long id;
    private String text;
    private String username;
    private Date tweeted;
    private Double latitude;
    private Double longitude;
    @Column(insertable = false, updatable = false)
    private Date created;

    public Date getCreated() {
        return this.created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
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
