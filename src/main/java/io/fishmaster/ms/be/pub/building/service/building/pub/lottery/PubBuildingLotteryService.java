package io.fishmaster.ms.be.pub.building.service.building.pub.lottery;

import io.fishmaster.ms.be.pub.building.web.dto.req.lottery.BuildingLotteryExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.lottery.BuildingLotteryUiDto;

public interface PubBuildingLotteryService {

    BuildingLotteryUiDto executeLottery(BuildingLotteryExecutedReqDto reqDto);

}
