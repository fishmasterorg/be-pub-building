package io.fishmaster.ms.be.pub.building.web.controller.exchanger;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer.PubBuildingExchangerOfferService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferGroupUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/exchanger/offer")
@RestController
public class BuildingExchangerOfferController {

    private final PubBuildingExchangerOfferService pubBuildingExchangerOfferService;

    @PostMapping("/fetch")
    public List<BuildingExchangerOfferGroupUiDto> fetch(@Valid @NotNull @RequestBody BuildingExchangerOfferFetchedReqDto reqDto) {
        log.info("Request to fetch exchanger offers in building received. {}", reqDto);
        var buildingExchangerOfferGroupUiDtos = pubBuildingExchangerOfferService.fetch(reqDto);
        log.info("Response on fetch exchanger offers in building. Size = {}", buildingExchangerOfferGroupUiDtos.size());
        return buildingExchangerOfferGroupUiDtos;
    }

    @PostMapping("/execute")
    public BuildingExchangerOfferUiDto execute(@Valid @NotNull @RequestBody BuildingExchangerOfferExecutedReqDto reqDto) {
        log.info("Request to execute exchanger offer in building received. {}", reqDto);
        var buildingBarmenOfferUiDto = pubBuildingExchangerOfferService.executeOffer(reqDto);
        log.info("Response on execute exchanger offer in building. {}", buildingBarmenOfferUiDto);
        return buildingBarmenOfferUiDto;
    }

}
