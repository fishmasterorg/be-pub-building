package io.fishmaster.ms.be.pub.building.web.dto.ui;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.PubBuildingBarmanUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.PubBuildingExchangerExchangeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.PubBuildingExchangerOfferUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.PubBuildingTradingChallengeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.PubBuildingTradingOrderUiDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PubBuildingUiDto extends BuildingUiDto {
    List<BuildingDailyTaskUiDto> dailyTasks;
    PubBuildingBarmanUiDto barman;
    PubBuildingTradingOrderUiDto tradingOrders;
    PubBuildingTradingChallengeUiDto tradingChallenges;
    PubBuildingExchangerExchangeUiDto exchangerExchange;
    PubBuildingExchangerOfferUiDto exchangerOffers;
}
