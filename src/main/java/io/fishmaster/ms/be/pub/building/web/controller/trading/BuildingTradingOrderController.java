package io.fishmaster.ms.be.pub.building.web.controller.trading;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.PubBuildingTradingOrderService;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderDoubleUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/trading/order")
@RestController
public class BuildingTradingOrderController {

    private final PubBuildingTradingOrderService pubBuildingTradingOrderService;

    @PostMapping("/fetch")
    public List<BuildingTradingOrderUiDto> fetch(@Valid @NotNull @RequestBody BuildingTradingOrderFetchedReqDto reqDto) {
        log.info("Request to fetch trading orders in building received. {}", reqDto);
        var buildingTradingOrderUiDtos = pubBuildingTradingOrderService.fetch(reqDto);
        log.info("Response on fetch trading orders in building. Size = {}", buildingTradingOrderUiDtos.size());
        return buildingTradingOrderUiDtos;
    }

    @PostMapping("/execute")
    public BuildingTradingOrderDoubleUiDto execute(@Valid @NotNull @RequestBody BuildingTradingOrderExecutedReqDto reqDto) {
        log.info("Request to execute trading order in building received. {}", reqDto);
        var buildingFoodTradingOrderDoubleUiDto = pubBuildingTradingOrderService.executeOrder(reqDto);
        log.info("Response on execute trading order in building. {}", buildingFoodTradingOrderDoubleUiDto);
        return buildingFoodTradingOrderDoubleUiDto;
    }

    @PostMapping("/cancel")
    public BuildingTradingOrderDoubleUiDto cancel(@Valid @NotNull @RequestBody BuildingTradingOrderCanceledReqDto reqDto) {
        log.info("Request to cancel trading order in building received. {}", reqDto);
        var buildingFoodTradingOrderDoubleUiDto = pubBuildingTradingOrderService.cancelOrder(reqDto);
        log.info("Response on cancel trading order in building. {}", buildingFoodTradingOrderDoubleUiDto);
        return buildingFoodTradingOrderDoubleUiDto;
    }

}
