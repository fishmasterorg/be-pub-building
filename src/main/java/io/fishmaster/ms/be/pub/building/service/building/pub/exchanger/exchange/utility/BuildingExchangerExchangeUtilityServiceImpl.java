package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility;

import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getCardRewardsCount;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getCharacterEnergy;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getCharacterExperiences;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getCraftTax;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getLockedCards;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.model.BuildingExchangerExchangeData;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BuildingExchangerExchangeUtilityServiceImpl implements BuildingExchangerExchangeUtilityService {

    private final ConfigurationsStorageService configurationsStorageService;

    @Override
    public BuildingExchangerExchangeData getBuildingExchangerExchangeData(
            BuildingExchangerExchangeExecutedReqDto reqDto, CityDto cityDto, CharacterDto characterDto,
            ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto, BuildingType buildingType) {
        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();

        var resultCard = recipeConfigurationDto.getResult().getCard();
        var accountCardDto = AccountCardDto.of(characterDto.getAccountId(), resultCard.getId(), buildingType);

        var buildingExchangeData = new BuildingExchangerExchangeData();
        buildingExchangeData.upsertLockedCards(
                getLockedCards(characterDto, cityDto, recipeConfigurationDto, buildingType));
        buildingExchangeData.upsertCraftedCards(AccountCardDto.of(accountCardDto));

        buildingExchangeData.upsertCraftTax(
                getCraftTax(cityDto.getCityTaxRate(), systemConfigurationDto, recipeConfigurationDto));
        buildingExchangeData.upsertCharacterEnergy(
                getCharacterEnergy(characterDto, recipeConfigurationDto, buildingType));
        buildingExchangeData.upsertExperiences(
                getCharacterExperiences(characterDto, recipeConfigurationDto, systemConfigurationDto, buildingType));

        var cardRewardCount = getCardRewardsCount(characterDto, recipeConfigurationDto.getLevel(), buildingType);
        IntStream.range(0, cardRewardCount)
                .mapToObj(index -> AccountCardDto.of(accountCardDto))
                .forEach(buildingExchangeData::upsertAdditionalCraftedCards);

        return buildingExchangeData;
    }

}
