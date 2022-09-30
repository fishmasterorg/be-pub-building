package io.fishmaster.ms.be.pub.building.service.configuration;

import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.PubBuildingConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer.BarmanOfferConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.ExchangerOfferConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.group.ExchangerOfferGroupConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery.LotteryPackConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level.TaskLevelConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;

public interface ConfigurationsStorageService {

    ExchangerExchangeRecipeConfigurationDtos getExchangerExchangeRecipeConfigurations();

    DailyTaskConfigurationDtos getDailyTaskConfigurations();

    TaskLevelConfigurationDtos getTaskLevelConfigurations();

    PubBuildingConfigurationDtos getPubBuildingConfigurations();

    BarmanOfferConfigurationDtos getBarmanOfferConfigurations();

    ExchangerOfferConfigurationDtos getExchangerOfferConfigurations();

    ExchangerOfferGroupConfigurationDtos getExchangerOfferGroupConfigurations();

    LotteryPackConfigurationDtos getLotteryPackConfigurations();

    TradingOrderConfigurationDtos getTradingOrderConfigurations();

    TradingOrderDataConfigurationDtos getTradingOrderDataConfigurations();

    TradingChallengeConfigurationDtos getTradingChallengeConfigurations();

    TradingChallengeDataConfigurationDtos getTradingChallengeDataConfigurations();

    SystemConfigurationDto getSystemConfiguration();

}
