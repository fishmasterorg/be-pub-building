package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.PubBuildingConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer.BarmanOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.ExchangerOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.group.ExchangerOfferGroupConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery.LotteryPackConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level.TaskLevelConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PubBuildingConfigurationStateDto {
    @ToString.Exclude
    List<ExchangerExchangeRecipeConfigurationDto> exchangerExchangeRecipeConfigurationDtos;
    @ToString.Exclude
    List<TaskLevelConfigurationDto> taskLevelConfigurationDtos;
    @ToString.Exclude
    List<DailyTaskConfigurationDto> dailyTaskConfigurationDtos;
    @ToString.Exclude
    List<PubBuildingConfigurationDto> pubBuildingConfigurationDtos;
    @ToString.Exclude
    List<BarmanOfferConfigurationDto> barmanOfferConfigurationDtos;
    @ToString.Exclude
    List<ExchangerOfferConfigurationDto> exchangerOfferConfigurationDtos;
    @ToString.Exclude
    List<ExchangerOfferGroupConfigurationDto> exchangerOfferGroupConfigurationDtos;
    @ToString.Exclude
    List<LotteryPackConfigurationDto> lotteryPackConfigurationDtos;
    @ToString.Exclude
    List<TradingOrderConfigurationDto> tradingOrderConfigurationDtos;
    @ToString.Exclude
    List<TradingOrderDataConfigurationDto> tradingOrderDataConfigurationDtos;
    @ToString.Exclude
    List<TradingChallengeConfigurationDto> tradingChallengeConfigurationDtos;
    @ToString.Exclude
    List<TradingChallengeDataConfigurationDto> tradingChallengeDataConfigurationDtos;

    SystemConfigurationDto systemConfigurationDto;
}
