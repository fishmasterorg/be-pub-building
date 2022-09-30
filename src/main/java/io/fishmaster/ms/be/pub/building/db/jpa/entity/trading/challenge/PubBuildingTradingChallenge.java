package io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.AbstractEntity;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.model.TradingChallengeData;
import io.fishmaster.ms.be.pub.building.db.jpa.field.TradingChallengeDataAttributeConverter;
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
        name = "pub_building_trading_challenge_sequence_generator",
        sequenceName = "pub_building_trading_challenge_sequence",
        allocationSize = 1)
@Table(name = "pub_building_trading_challenge")
public class PubBuildingTradingChallenge extends AbstractEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pub_building_trading_challenge_sequence_generator")
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "configuration_id", nullable = false)
    private String configurationId;
    @Convert(converter = TradingChallengeDataAttributeConverter.class)
    @Column(name = "data", columnDefinition = "json", nullable = false)
    private TradingChallengeData data;
    @Column(name = "challenge_ended_time", nullable = false)
    private Long challengeEndedTime;
}
