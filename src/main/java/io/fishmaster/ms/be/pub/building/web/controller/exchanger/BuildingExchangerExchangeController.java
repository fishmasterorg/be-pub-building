package io.fishmaster.ms.be.pub.building.web.controller.exchanger;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.PubBuildingExchangerExchangeService;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.recipe.PubBuildingExchangerExchangeRecipeService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeRecipeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.BuildingExchangerExchangeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.recipe.BuildingExchangerExchangeRecipeUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/exchanger/exchange")
@RestController
public class BuildingExchangerExchangeController {

    private final PubBuildingExchangerExchangeService pubBuildingExchangerExchangeService;
    private final PubBuildingExchangerExchangeRecipeService pubBuildingExchangerExchangeRecipeService;

    @PostMapping("/execute")
    public BuildingExchangerExchangeUiDto start(@Valid @NotNull @RequestBody BuildingExchangerExchangeExecutedReqDto reqDto) {
        log.info("Request to execute exchanger exchange in building received. {}", reqDto);
        var buildingExchangerExchangeUiDto = pubBuildingExchangerExchangeService.executeExchange(reqDto);
        log.info("Response on execute exchanger exchange in building. {}", buildingExchangerExchangeUiDto);
        return buildingExchangerExchangeUiDto;
    }

    @PostMapping("/recipe/fetch")
    public List<BuildingExchangerExchangeRecipeUiDto> fetch(@Valid @NotNull @RequestBody BuildingExchangerExchangeRecipeFetchedReqDto reqDto) {
        log.info("Request to fetch all exchanger exchange recipe for building received. {}", reqDto);
        var buildingResourceRecipeUiDtos = pubBuildingExchangerExchangeRecipeService.fetch(reqDto);
        log.info("Response on fetch all exchanger exchange recipe for building. Size = {}", buildingResourceRecipeUiDtos.size());
        return buildingResourceRecipeUiDtos;
    }

}
