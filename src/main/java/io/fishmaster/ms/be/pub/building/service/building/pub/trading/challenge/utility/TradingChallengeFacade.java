package io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.utility;

import java.time.Clock;
import java.util.List;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.PubBuildingTradingChallenge;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.model.TradingChallengeData;
import io.fishmaster.ms.be.pub.building.db.share.TradingChallengeCheckpoint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradingChallengeFacade {
    TradingChallengeConfigurationDto configurationDto;
    TradingChallengeDataConfigurationDto dataConfigurationDto;
    Integer cardsQuantity;
    Long challengeEndedTime;
    List<TradingChallengeCheckpoint> checkpoints;
    Double cost;
    Double tax;
    List<ResultExperience> experiences;

    public Long getCardId() {
        return getDataConfigurationDto().getCardId();
    }

    public TradingChallengeData getFoodChallengeData() {
        var dataConfigurationDto = getDataConfigurationDto();
        return new TradingChallengeData(
                dataConfigurationDto.getId(), getCardsQuantity());
    }

    public static TradingChallengeFacade buildWithoutExperiences(
            Double systemTaxRate, Double cityTaxRate, PubBuildingTradingChallenge pubBuildingFoodChallenge,
            TradingChallengeDataConfigurationDtos dataConfigurationDtos, TradingChallengeConfigurationDtos configurationDtos,
            Clock utcClock) {
        var data = pubBuildingFoodChallenge.getData();

        var dataConfigurationDto = dataConfigurationDtos.getById(data.getConfigurationId());
        var configurationDto = configurationDtos.getById(pubBuildingFoodChallenge.getConfigurationId());

        return buildWithoutExperiences(
                systemTaxRate, cityTaxRate, data.getCardsQuantity(), pubBuildingFoodChallenge.getChallengeEndedTime(),
                dataConfigurationDto, configurationDto, utcClock);
    }

    public static TradingChallengeFacade buildWithoutExperiences(
            TradingChallengeDataConfigurationDtos dataConfigurationDtos, TradingChallengeConfigurationDto configurationDto,
            Clock utcClock) {
        var dataConfigurationDto = TradingChallengeFacadeUtility.determineDataConfiguration(dataConfigurationDtos);

        var cardsQuantity = TradingChallengeFacadeUtility.determineCardsQuantity(configurationDto);
        var challengeEndedTime = TradingChallengeFacadeUtility.determineChallengeEndedTime(configurationDto, utcClock);
        return buildWithoutExperiences(
                0D, 0D, cardsQuantity, challengeEndedTime, dataConfigurationDto, configurationDto, utcClock);
    }

    public static TradingChallengeFacade buildWithoutExperiences(
            Double systemTaxRate, Double cityTaxRate, Integer cardsQuantity, Long challengeEndedTime,
            TradingChallengeDataConfigurationDto dataConfigurationDto, TradingChallengeConfigurationDto configurationDto,
            Clock utcClock) {
        var checkpoints = TradingChallengeFacadeUtility.determineCheckpoints(
                cardsQuantity, challengeEndedTime, dataConfigurationDto.getCost(), systemTaxRate, cityTaxRate,
                configurationDto);
        var cost = TradingChallengeFacadeUtility.determineCost(checkpoints, utcClock);
        var tax = TradingChallengeFacadeUtility.determineTax(checkpoints, utcClock);

        return new TradingChallengeFacade(
                configurationDto, dataConfigurationDto, cardsQuantity, challengeEndedTime, checkpoints,
                cost, tax, List.of());
    }

    public static TradingChallengeFacade buildWithExperiences(
            CharacterDto characterDto, Double cityTaxRate, PubBuildingTradingChallenge pubBuildingFoodChallenge,
            TradingChallengeDataConfigurationDtos dataConfigurationDtos, TradingChallengeConfigurationDto configurationDto,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType, Clock utcClock) {
        var data = pubBuildingFoodChallenge.getData();
        var dataConfigurationDto = dataConfigurationDtos.getById(data.getConfigurationId());

        return buildWithExperiences(
                characterDto, cityTaxRate, data.getCardsQuantity(), pubBuildingFoodChallenge.getChallengeEndedTime(),
                dataConfigurationDto, configurationDto, systemConfigurationDto, buildingType, utcClock);
    }

    public static TradingChallengeFacade buildWithExperiences(
            CharacterDto characterDto, Double cityTaxRate, Integer cardsQuantity, Long challengeEndedTime,
            TradingChallengeDataConfigurationDto dataConfigurationDto, TradingChallengeConfigurationDto configurationDto,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType, Clock utcClock) {
        var checkpoints = TradingChallengeFacadeUtility.determineCheckpoints(
                cardsQuantity, challengeEndedTime, dataConfigurationDto.getCost(), systemConfigurationDto.getTaxRate(),
                cityTaxRate.doubleValue(), configurationDto);
        var cost = TradingChallengeFacadeUtility.determineCost(checkpoints, utcClock);
        var tax = TradingChallengeFacadeUtility.determineTax(checkpoints, utcClock);
        var characterExperiences = TradingChallengeFacadeUtility.determineExperiences(
                cardsQuantity, characterDto, dataConfigurationDto, systemConfigurationDto, buildingType);

        return new TradingChallengeFacade(
                configurationDto, dataConfigurationDto, cardsQuantity, challengeEndedTime, checkpoints,
                cost, tax, characterExperiences);
    }

}

