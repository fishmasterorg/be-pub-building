package io.fishmaster.ms.be.pub.building.db.jpa.entity.task;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.AbstractEntity;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.field.SetLongAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(
        name = "pub_building_daily_task_sequence_generator",
        sequenceName = "pub_building_daily_task_sequence",
        allocationSize = 1)
@Table(name = "pub_building_daily_task")
public class PubBuildingDailyTask extends AbstractEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pub_building_daily_task_sequence_generator")
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private String accountId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    private PubBuilding pubBuilding;
    @Convert(converter = SetLongAttributeConverter.class)
    @Column(name = "character_ids", nullable = false)
    private Set<Long> characterIds;
    @Column(name = "configuration_id", nullable = false)
    private String configurationId;
    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress;
    @Column(name = "final_progress", nullable = false)
    private Integer finalProgress;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
