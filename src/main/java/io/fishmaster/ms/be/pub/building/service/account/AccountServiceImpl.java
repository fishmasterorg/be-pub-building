package io.fishmaster.ms.be.pub.building.service.account;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.commons.kafka.dto.account.KafkaAccount;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.req.CityFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.PubBuildingDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.prepared.PubBuildingPreparedDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.trading.order.PubBuildingTradingOrderRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.barman.offer.PubBuildingBarmanOfferHistoryRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.exchanger.exchange.PubBuildingExchangerExchangeHistoryRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.lottery.PubBuildingLotteryHistoryRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.task.PubBuildingDailyTaskHistoryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    
    private final CityCommunicationService cityCommunicationService;

    private final PubBuildingRepository pubBuildingRepository;
    private final PubBuildingPreparedDailyTaskRepository pubBuildingPreparedDailyTaskRepository;
    private final PubBuildingDailyTaskRepository pubBuildingDailyTaskRepository;
    private final PubBuildingDailyTaskHistoryRepository pubBuildingDailyTaskHistoryRepository;
    private final PubBuildingBarmanOfferHistoryRepository pubBuildingBarmanOfferHistoryRepository;
    private final PubBuildingLotteryHistoryRepository pubBuildingLotteryHistoryRepository;
    private final PubBuildingTradingOrderRepository pubBuildingTradingOrderRepository;
    private final PubBuildingExchangerExchangeHistoryRepository pubBuildingEquipmentExchangeHistoryRepository;

    @Transactional
    @Override
    public void init(KafkaAccount kafkaAccount) {

    }

    @Transactional
    @Override
    public void remove(KafkaAccount kafkaAccount) {
        var cityIds = cityCommunicationService.fetchAllCity(CityFetchedReqDto.of(kafkaAccount.getId())).stream()
                .map(CityDto::getId)
                .collect(Collectors.toSet());

        pubBuildingEquipmentExchangeHistoryRepository.deleteAllByAccountId(kafkaAccount.getId());
        pubBuildingTradingOrderRepository.deleteAllByPubBuilding_CityIdIn(cityIds);
        pubBuildingLotteryHistoryRepository.deleteAllByAccountId(kafkaAccount.getId());
        pubBuildingBarmanOfferHistoryRepository.deleteAllByAccountId(kafkaAccount.getId());
        pubBuildingDailyTaskHistoryRepository.deleteAllByAccountId(kafkaAccount.getId());
        pubBuildingDailyTaskRepository.deleteAllByPubBuilding_CityIdIn(cityIds);
        pubBuildingPreparedDailyTaskRepository.deleteAllByPubBuilding_CityIdIn(cityIds);
        pubBuildingRepository.deleteAllByCityIdIn(cityIds);
    }
}
