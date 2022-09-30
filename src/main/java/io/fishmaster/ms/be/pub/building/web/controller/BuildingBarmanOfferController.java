package io.fishmaster.ms.be.pub.building.web.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer.PubBuildingBarmanOfferService;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.offer.BuildingBarmanOfferUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/barman/offer")
@RestController
public class BuildingBarmanOfferController {

    private final PubBuildingBarmanOfferService pubBuildingBarmanOfferService;

    @PostMapping("/fetch")
    public List<BuildingBarmanOfferUiDto> fetch(@Valid @NotNull @RequestBody BuildingBarmanOfferFetchedReqDto reqDto) {
        log.info("Request to fetch barman offers in building received. {}", reqDto);
        var buildingBarmenOfferUiDtos = pubBuildingBarmanOfferService.fetch(reqDto);
        log.info("Response on fetch barman offers in building. Size = {}", buildingBarmenOfferUiDtos.size());
        return buildingBarmenOfferUiDtos;
    }

    @PostMapping("/execute")
    public BuildingBarmanOfferUiDto execute(@Valid @NotNull @RequestBody BuildingBarmanOfferExecutedReqDto reqDto) {
        log.info("Request to execute barman offer in building received. {}", reqDto);
        var buildingBarmenOfferUiDto = pubBuildingBarmanOfferService.executeOffer(reqDto);
        log.info("Response on execute barman offer in building. {}", buildingBarmenOfferUiDto);
        return buildingBarmenOfferUiDto;
    }

}
