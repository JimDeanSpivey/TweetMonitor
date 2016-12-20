package io.crowdsignal.entities;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Jimmy Spivey
 */
public abstract class AuditColumnEntity {

    @Column(insertable = false, updatable = false)
    private Date created;
    private Date updated;

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
