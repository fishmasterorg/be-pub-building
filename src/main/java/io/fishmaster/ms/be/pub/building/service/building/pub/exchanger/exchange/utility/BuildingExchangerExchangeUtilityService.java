package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.model.BuildingExchangerExchangeData;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;

public interface BuildingExchangerExchangeUtilityService {

    BuildingExchangerExchangeData getBuildingExchangerExchangeData(
            BuildingExchangerExchangeExecutedReqDto reqDto, CityDto cityDto, CharacterDto characterDto,
            ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto, BuildingType buildingType);

}
