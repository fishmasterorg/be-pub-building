package io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.AbstractEntity;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.model.TradingOrderData;
import io.fishmaster.ms.be.pub.building.db.jpa.field.TradingOrderDataAttributeConverter;
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
        name = "pub_building_trading_order_sequence_generator",
        sequenceName = "pub_building_trading_order_sequence",
        allocationSize = 1)
@Table(name = "pub_building_trading_order")
public class PubBuildingTradingOrder extends AbstractEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pub_building_trading_order_sequence_generator")
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private String accountId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    private PubBuilding pubBuilding;
    @Column(name = "configuration_id", nullable = false)
    private String configurationId;
    @Convert(converter = TradingOrderDataAttributeConverter.class)
    @Column(name = "data", columnDefinition = "json", nullable = false)
    private TradingOrderData data;
}
