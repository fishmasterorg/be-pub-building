package io.fishmaster.ms.be.pub.building.db.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
        name = "pub_building_sequence_generator",
        sequenceName = "pub_building_sequence",
        allocationSize = 1)
@Table(name = "pub_building")
public class PubBuilding extends AbstractEntity implements Building {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pub_building_sequence_generator")
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "city_id", nullable = false)
    private Long cityId;
    @Column(name = "level", nullable = false)
    private Integer level;
}
