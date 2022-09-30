package io.fishmaster.ms.be.pub.building.db.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Clock;
import java.util.Objects;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbstractEntity {

    protected static final Clock CLOCK = Clock.systemUTC();

    @Column(name = "created_date", nullable = false)
    protected Long createdDate;
    @Column(name = "last_modified_date", nullable = false)
    protected Long lastModifiedDate;

    @PrePersist
    protected void handleBeforeSave() {
        var now = CLOCK.millis();
        if (Objects.isNull(getCreatedDate())) setCreatedDate(now);
        if (Objects.isNull(getLastModifiedDate())) setLastModifiedDate(now);
    }

    @PreUpdate
    protected void handleBeforeUpdate() {
        var now = CLOCK.millis();
        setLastModifiedDate(now);
    }

}
