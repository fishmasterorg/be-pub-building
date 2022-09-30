package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferGroupUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.PubBuildingExchangerOfferUiDto;

public interface PubBuildingExchangerOfferService {

    PubBuildingExchangerOfferUiDto getExchangerOffer(String accountId, PubBuilding pubBuilding);

    default CompletableFuture<PubBuildingExchangerOfferUiDto> getAsyncExchangerOffer(
            String accountId, PubBuilding pubBuilding, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getExchangerOffer(accountId, pubBuilding), executor);
    }

    List<BuildingExchangerOfferGroupUiDto> fetch(BuildingExchangerOfferFetchedReqDto reqDto);

    BuildingExchangerOfferUiDto executeOffer(BuildingExchangerOfferExecutedReqDto reqDto);

}
