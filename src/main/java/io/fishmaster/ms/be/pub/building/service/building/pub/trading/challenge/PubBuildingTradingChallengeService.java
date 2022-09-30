package io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.commons.dto.PageUiDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeHistoryUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.PubBuildingTradingChallengeUiDto;

public interface PubBuildingTradingChallengeService {

    PubBuildingTradingChallengeUiDto getTradingChallenge(String accountId, PubBuilding pubBuilding);

    default CompletableFuture<PubBuildingTradingChallengeUiDto> getAsyncTradingChallenge(
            String accountId, PubBuilding pubBuilding, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getTradingChallenge(accountId, pubBuilding), executor);
    }

    BuildingTradingChallengeUiDto fetchActive(BuildingTradingChallengeFetchedReqDto reqDto);

    PageUiDto<BuildingTradingChallengeHistoryUiDto> fetchHistory(Integer page, Integer size);

    BuildingTradingChallengeHistoryUiDto executeChallenge(BuildingTradingChallengeExecutedReqDto reqDto);

    void moveChallengeToHistoryWhenExpire();

}
