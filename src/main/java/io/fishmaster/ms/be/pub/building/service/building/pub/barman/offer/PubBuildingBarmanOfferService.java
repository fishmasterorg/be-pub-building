package io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.PubBuildingBarmanUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.offer.BuildingBarmanOfferUiDto;

public interface PubBuildingBarmanOfferService {

    PubBuildingBarmanUiDto getBarman(String accountId, PubBuilding pubBuilding);

    default CompletableFuture<PubBuildingBarmanUiDto> getAsyncBarman(String accountId, PubBuilding pubBuilding, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getBarman(accountId, pubBuilding), executor);
    }

    List<BuildingBarmanOfferUiDto> fetch(BuildingBarmanOfferFetchedReqDto reqDto);

    BuildingBarmanOfferUiDto executeOffer(BuildingBarmanOfferExecutedReqDto reqDto);

}
