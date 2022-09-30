package io.fishmaster.ms.be.pub.building.service.configuration;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

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
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.service.ConfigurationsStorageCommunicationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConfigurationsStorageServiceImpl implements ConfigurationsStorageService {

    private final ConfigurationsStorageCommunicationService configurationsStorageCommunicationService;

    @Override
    public ExchangerExchangeRecipeConfigurationDtos getExchangerExchangeRecipeConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return ExchangerExchangeRecipeConfigurationDtos.of(Streamable.of(state.getExchangerExchangeRecipeConfigurationDtos()));
    }

    @Override
    public DailyTaskConfigurationDtos getDailyTaskConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return DailyTaskConfigurationDtos.of(Streamable.of(state.getDailyTaskConfigurationDtos()));
    }

    @Override
    public TaskLevelConfigurationDtos getTaskLevelConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return TaskLevelConfigurationDtos.of(Streamable.of(state.getTaskLevelConfigurationDtos()));
    }

    @Override
    public PubBuildingConfigurationDtos getPubBuildingConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return PubBuildingConfigurationDtos.of(Streamable.of(state.getPubBuildingConfigurationDtos()));
    }

    @Override
    public BarmanOfferConfigurationDtos getBarmanOfferConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return BarmanOfferConfigurationDtos.of(Streamable.of(state.getBarmanOfferConfigurationDtos()));
    }

    @Override
    public ExchangerOfferConfigurationDtos getExchangerOfferConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return ExchangerOfferConfigurationDtos.of(Streamable.of(state.getExchangerOfferConfigurationDtos()));
    }

    @Override
    public ExchangerOfferGroupConfigurationDtos getExchangerOfferGroupConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return ExchangerOfferGroupConfigurationDtos.of(Streamable.of(state.getExchangerOfferGroupConfigurationDtos()));
    }

    @Override
    public LotteryPackConfigurationDtos getLotteryPackConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return LotteryPackConfigurationDtos.of(Streamable.of(state.getLotteryPackConfigurationDtos()));
    }

    @Override
    public TradingOrderConfigurationDtos getTradingOrderConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return TradingOrderConfigurationDtos.of(Streamable.of(state.getTradingOrderConfigurationDtos()));
    }

    @Override
    public TradingOrderDataConfigurationDtos getTradingOrderDataConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return TradingOrderDataConfigurationDtos.of(Streamable.of(state.getTradingOrderDataConfigurationDtos()));
    }

    @Override
    public TradingChallengeConfigurationDtos getTradingChallengeConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return TradingChallengeConfigurationDtos.of(Streamable.of(state.getTradingChallengeConfigurationDtos()));
    }

    @Override
    public TradingChallengeDataConfigurationDtos getTradingChallengeDataConfigurations() {
        var state = configurationsStorageCommunicationService.getState();

        return TradingChallengeDataConfigurationDtos.of(Streamable.of(state.getTradingChallengeDataConfigurationDtos()));
    }

    @Override
    public SystemConfigurationDto getSystemConfiguration() {
        var state = configurationsStorageCommunicationService.getState();

        return state.getSystemConfigurationDto();
    }

}
