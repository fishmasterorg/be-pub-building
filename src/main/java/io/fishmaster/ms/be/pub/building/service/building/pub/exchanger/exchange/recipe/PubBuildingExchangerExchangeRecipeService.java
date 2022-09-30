package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.recipe;

import java.util.List;

import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeRecipeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.recipe.BuildingExchangerExchangeRecipeUiDto;

public interface PubBuildingExchangerExchangeRecipeService {

    List<BuildingExchangerExchangeRecipeUiDto> fetch(BuildingExchangerExchangeRecipeFetchedReqDto reqDto);

}
