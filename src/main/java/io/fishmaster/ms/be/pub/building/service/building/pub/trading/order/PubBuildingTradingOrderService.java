package io.fishmaster.ms.be.pub.building.service.building.pub.trading.order;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderDoubleUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.PubBuildingTradingOrderUiDto;

public interface PubBuildingTradingOrderService {

    PubBuildingTradingOrderUiDto getTradingOrder(String accountId, PubBuilding pubBuilding);

    default CompletableFuture<PubBuildingTradingOrderUiDto> getAsyncTradingOrder(String accountId, PubBuilding pubBuilding, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getTradingOrder(accountId, pubBuilding), executor);
    }

    List<BuildingTradingOrderUiDto> fetch(BuildingTradingOrderFetchedReqDto reqDto);

    BuildingTradingOrderDoubleUiDto executeOrder(BuildingTradingOrderExecutedReqDto reqDto);

    BuildingTradingOrderDoubleUiDto cancelOrder(BuildingTradingOrderCanceledReqDto reqDto);

}
