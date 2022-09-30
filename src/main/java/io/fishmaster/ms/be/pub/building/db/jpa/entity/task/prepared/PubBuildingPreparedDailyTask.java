package io.fishmaster.ms.be.pub.building.db.jpa.entity.task.prepared;

import javax.persistence.Column;
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

import io.fishmaster.ms.be.commons.constant.task.WeekDay;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.AbstractEntity;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
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
        name = "pub_building_prepared_daily_task_sequence_generator",
        sequenceName = "pub_building_prepared_daily_task_sequence",
        allocationSize = 1
)
@Table(name = "pub_building_prepared_daily_task")
public class PubBuildingPreparedDailyTask extends AbstractEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pub_building_prepared_daily_task_sequence_generator"
    )
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    private PubBuilding pubBuilding;
    @Enumerated(EnumType.STRING)
    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;
    @Column(name = "configuration_id", nullable = false)
    private String configurationId;
}
