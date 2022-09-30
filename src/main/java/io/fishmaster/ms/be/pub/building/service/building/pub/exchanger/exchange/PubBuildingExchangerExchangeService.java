package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.BuildingExchangerExchangeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.PubBuildingExchangerExchangeUiDto;

public interface PubBuildingExchangerExchangeService {

    PubBuildingExchangerExchangeUiDto getExchangerExchange(String accountId, PubBuilding pubBuilding);

    default CompletableFuture<PubBuildingExchangerExchangeUiDto> getAsyncExchangerExchange(
            String accountId, PubBuilding pubBuilding, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getExchangerExchange(accountId, pubBuilding), executor);
    }

    BuildingExchangerExchangeUiDto executeExchange(BuildingExchangerExchangeExecutedReqDto reqDto);

}
