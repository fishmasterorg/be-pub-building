package io.fishmaster.ms.be.pub.building.service.building.pub.trading.order;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.fishmaster.ms.be.commons.constant.Currency;
import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.operation.OperationType;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.service.AccountBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.CardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.service.CityBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.trading.order.PubBuildingTradingOrderConverter;
import io.fishmaster.ms.be.pub.building.converter.trading.order.PubBuildingTradingOrderHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.PubBuildingTradingOrder;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.trading.order.PubBuildingTradingOrderRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order.PubBuildingTradingOrderHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order.aggregation.PubBuildingTradingOrderHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.trading.order.PubBuildingTradingOrderHistoryRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.model.BuildingTradingOrderProcessCanceled;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.model.BuildingTradingOrderProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.utility.TradingOrderFacade;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.order.BuildingTradingOrderFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderDoubleUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.PubBuildingTradingOrderUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingTradingOrderServiceImpl implements PubBuildingTradingOrderService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final CharacterCommunicationService characterCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;
    private final AccountBalanceCommunicationService accountBalanceCommunicationService;
    private final CityBalanceCommunicationService cityBalanceCommunicationService;
    private final CityCommunicationService cityCommunicationService;

    private final PubBuildingDailyTaskKafkaSender pubBuildingDailyTaskKafkaSender;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;
    private final PubBuildingTradingOrderRepository pubBuildingTradingOrderRepository;
    private final PubBuildingTradingOrderHistoryRepository pubBuildingTradingOrderHistoryRepository;

    private final Clock utcClock;

    @Override
    public PubBuildingTradingOrderUiDto getTradingOrder(String accountId, PubBuilding pubBuilding) {
        var uniqueMappedResult = pubBuildingTradingOrderHistoryRepository.aggregateByAccountIdAndBuildingId(accountId, pubBuilding.getId())
                .getUniqueMappedResult();

        var lastCharacterIdUsed = Optional.ofNullable(uniqueMappedResult)
                .map(PubBuildingTradingOrderHistoryGroupedByBuildingId::getRecord)
                .map(PubBuildingTradingOrderHistory::getCharacterId)
                .filter(characterId -> characterCommunicationService.existsCharacter(characterId, accountId, pubBuilding.getCityId()))
                .orElse(null);

        var pubBuildingTradingOrderUiDtos =
                fetch(new BuildingTradingOrderFetchedReqDto(accountId, lastCharacterIdUsed, pubBuilding.getId()));

        var lastCharacterIdsUsed = Objects.isNull(lastCharacterIdUsed) ? Set.<Long>of() : Set.of(lastCharacterIdUsed);

        return new PubBuildingTradingOrderUiDto(lastCharacterIdsUsed, pubBuildingTradingOrderUiDtos);
    }

    @Transactional
    @Override
    public List<BuildingTradingOrderUiDto> fetch(BuildingTradingOrderFetchedReqDto reqDto) {
        var configurationDtos = configurationsStorageService.getTradingOrderConfigurations();

        var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

        if (Objects.isNull(reqDto.getCharacterId())) {
            return fetchWithoutCharacter(reqDto, pubBuilding, configurationDtos);
        }
        return fetchWithCharacter(reqDto, pubBuilding, configurationDtos);
    }

    @Transactional
    @Override
    public BuildingTradingOrderDoubleUiDto executeOrder(BuildingTradingOrderExecutedReqDto reqDto) {
        var buildingTradingOrderProcessExecuted = new BuildingTradingOrderProcessExecuted(reqDto.getAccountId());
        try {
            var oldPubBuildingTradingOrder = pubBuildingTradingOrderRepository
                    .findWithLockByIdAndAccountId(reqDto.getBuildingTradingOrderId(), reqDto.getAccountId())
                    .orElseThrow(() -> new ServiceException(
                            ExceptionCode.INNER_SERVICE,
                            "Pub building trading order with id = %s and account id = %s not exists"
                                    .formatted(reqDto.getBuildingTradingOrderId(), reqDto.getAccountId())));

            var pubBuilding = oldPubBuildingTradingOrder.getPubBuilding();

            var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

            var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
            var dataConfigurationDtos = configurationsStorageService.getTradingOrderDataConfigurations();
            var configurationDtos = configurationsStorageService.getTradingOrderConfigurations();

            var characterDto = characterCommunicationService.lockCharacterStatus(
                    reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), Status.FREE,
                    CharacterParamDto.of(false, true, true, true),
                    buildingTradingOrderProcessExecuted::setCharacterStatus);

            var oldTradingOrderFacade = TradingOrderFacade.buildWithExperiences(
                    characterDto, cityDto, oldPubBuildingTradingOrder, dataConfigurationDtos, configurationDtos,
                    systemConfigurationDto, BUILDING_TYPE);

            checkOrderRequirements(characterDto, oldTradingOrderFacade.getConfigurationDto());

            cardInventoryCommunicationService.lockAccountCards(
                    AccountCardLockedReqDto.of(
                            buildingTradingOrderProcessExecuted.getAccountId(), cityDto.getId(), oldTradingOrderFacade.getCardId(),
                            oldTradingOrderFacade.getCardsQuantity(), CardType.FOOD, Status.ACTIVE),
                    oldTradingOrderFacade.getCardsQuantity(), buildingTradingOrderProcessExecuted::setLockedCards);

            subtractCharacterEnergy(
                    characterDto.getAccountId(), characterDto.getId(), oldTradingOrderFacade.getEnergyForDeliver(),
                    AuditAction.ORDER_EXECUTE, buildingTradingOrderProcessExecuted::setCharacterEnergyOperationRes);

            cityBalanceCommunicationService.handleOperation(
                    CityBalanceOperationReqDto.of(
                            cityDto.getId(), OperationType.ADD, Currency.GOLD, oldTradingOrderFacade.getTax(),
                            AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.ORDER_EXECUTE),
                    buildingTradingOrderProcessExecuted::setCityBalanceOperationReq);

            accountBalanceCommunicationService.handleOperation(
                    AccountBalanceOperationReqDto.of(
                            reqDto.getAccountId(), OperationType.ADD, Currency.GOLD, oldTradingOrderFacade.getCost(),
                            AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.ORDER_EXECUTE),
                    buildingTradingOrderProcessExecuted::setAccountBalanceOperationReq);

            characterDto = characterCommunicationService.addExperiences(
                    reqDto.getAccountId(), reqDto.getCharacterId(), oldTradingOrderFacade.getExperiences(),
                    AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.ORDER_EXECUTE);

            var newTradingOrderFacade =
                    reGenerateOrderFacade(
                            characterDto, cityDto, pubBuilding, oldTradingOrderFacade.getConfigurationDto(), systemConfigurationDto);
            var newPubBuildingTradingOrder = pubBuildingTradingOrderRepository.save(
                    PubBuildingTradingOrderConverter.toEntity(reqDto.getAccountId(), pubBuilding, newTradingOrderFacade));

            pubBuildingTradingOrderRepository.delete(oldPubBuildingTradingOrder);

            var pubBuildingTradingOrderHistory = pubBuildingTradingOrderHistoryRepository.save(
                    PubBuildingTradingOrderHistoryConverter.toEntityForExecute(
                            characterDto.getId(), oldPubBuildingTradingOrder, oldTradingOrderFacade,
                            buildingTradingOrderProcessExecuted.getAccountBalanceOperationReq(), Status.DONE, utcClock));

            buildingTradingOrderProcessExecuted.updateCharacterStatus(Status.FREE);
            buildingTradingOrderProcessExecuted.updateLockedCards(Status.USED, CardSource.PUB_TRADING_ORDER);

            buildingTradingOrderProcessExecuted.complete(
                    characterCommunicationService::completeCharacterStatus,
                    cardInventoryCommunicationService::completeAccountCards);

            pubBuildingDailyTaskKafkaSender.refreshEnergyTaskProgress(
                    TaskGoalType.SPENT_ENERGY, buildingTradingOrderProcessExecuted.getAccountId(), characterDto.getId(), cityDto.getId(),
                    BUILDING_TYPE, oldTradingOrderFacade.getEnergyForDeliver().intValue());

            return PubBuildingTradingOrderConverter.toDoubleUiDto(
                    PubBuildingTradingOrderHistoryConverter.toUiDto(pubBuildingTradingOrderHistory, oldTradingOrderFacade),
                    PubBuildingTradingOrderConverter.toUiDto(newPubBuildingTradingOrder, newTradingOrderFacade));
        } catch (Exception e) {
            log.error("Error while on execute building trading order. {}. Error = {}", reqDto, e.getMessage());

            buildingTradingOrderProcessExecuted.rollback(
                    cityBalanceCommunicationService::handleOperation,
                    accountBalanceCommunicationService::handleOperation,
                    characterCommunicationService::handleCharacterEnergyOperation,
                    cardInventoryCommunicationService::rollbackAccountCards,
                    characterCommunicationService::rollbackCharacterStatus);

            throw e;
        }
    }

    @Transactional
    @Override
    public BuildingTradingOrderDoubleUiDto cancelOrder(BuildingTradingOrderCanceledReqDto reqDto) {
        var buildingTradingOrderProcessCanceled = new BuildingTradingOrderProcessCanceled(reqDto.getAccountId());
        try {
            var oldPubBuildingTradingOrder =
                    pubBuildingTradingOrderRepository
                            .findWithLockByIdAndAccountId(reqDto.getBuildingTradingOrderId(), reqDto.getAccountId())
                            .orElseThrow(() -> new ServiceException(
                                    ExceptionCode.INNER_SERVICE,
                                    "Pub building trading order with id = %s and account id = %s not exists"
                                            .formatted(reqDto.getBuildingTradingOrderId(), reqDto.getAccountId())));

            var pubBuilding = oldPubBuildingTradingOrder.getPubBuilding();

            var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

            var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
            var dataConfigurationDtos = configurationsStorageService.getTradingOrderDataConfigurations();
            var configurationDtos = configurationsStorageService.getTradingOrderConfigurations();

            var characterDto = characterCommunicationService.lockCharacterStatus(
                    reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), Status.FREE,
                    CharacterParamDto.of(false, true, true, true),
                    buildingTradingOrderProcessCanceled::setCharacterStatus);

            var oldTradingOrderFacade = TradingOrderFacade.buildWithExperiences(
                    characterDto, cityDto, oldPubBuildingTradingOrder, dataConfigurationDtos, configurationDtos,
                    systemConfigurationDto, BUILDING_TYPE);

            checkOrderRequirements(characterDto, oldTradingOrderFacade.getConfigurationDto());

            subtractCharacterEnergy(
                    characterDto.getAccountId(), characterDto.getId(), oldTradingOrderFacade.getEnergyForCancel(),
                    AuditAction.ORDER_CANCEL, buildingTradingOrderProcessCanceled::setCharacterEnergyOperationRes);

            var newTradingOrderFacade =
                    reGenerateOrderFacade(
                            characterDto, cityDto, pubBuilding, oldTradingOrderFacade.getConfigurationDto(), systemConfigurationDto);
            var newPubBuildingTradingOrder = pubBuildingTradingOrderRepository.save(
                    PubBuildingTradingOrderConverter.toEntity(reqDto.getAccountId(), pubBuilding, newTradingOrderFacade));

            pubBuildingTradingOrderRepository.delete(oldPubBuildingTradingOrder);

            var pubBuildingTradingOrderHistory = pubBuildingTradingOrderHistoryRepository.save(
                    PubBuildingTradingOrderHistoryConverter.toEntityForCancel(
                            characterDto.getId(), oldPubBuildingTradingOrder, oldTradingOrderFacade, Status.CANCELED, utcClock));

            buildingTradingOrderProcessCanceled.updateCharacterStatus(Status.FREE);
            buildingTradingOrderProcessCanceled.complete(characterCommunicationService::completeCharacterStatus);

            pubBuildingDailyTaskKafkaSender.refreshEnergyTaskProgress(
                    TaskGoalType.SPENT_ENERGY, buildingTradingOrderProcessCanceled.getAccountId(), characterDto.getId(), cityDto.getId(),
                    BUILDING_TYPE, oldTradingOrderFacade.getEnergyForCancel().intValue());

            return PubBuildingTradingOrderConverter.toDoubleUiDto(
                    PubBuildingTradingOrderHistoryConverter.toUiDto(pubBuildingTradingOrderHistory, oldTradingOrderFacade),
                    PubBuildingTradingOrderConverter.toUiDto(newPubBuildingTradingOrder, newTradingOrderFacade));
        } catch (Exception e) {
            log.error("Error while on cancel building trading order. {}. Error = {}", reqDto, e.getMessage());

            buildingTradingOrderProcessCanceled.rollback(
                    characterCommunicationService::handleCharacterEnergyOperation,
                    characterCommunicationService::rollbackCharacterStatus);

            throw e;
        }
    }

    private List<BuildingTradingOrderUiDto> fetchWithCharacter(
            BuildingTradingOrderFetchedReqDto reqDto, PubBuilding pubBuilding,
            TradingOrderConfigurationDtos configurationDtos) {
        var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
        var dataConfigurationDtos = configurationsStorageService.getTradingOrderDataConfigurations();

        var characterDto = characterCommunicationService.fetchCharacter(
                reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(),
                CharacterParamDto.of(false, true, true, true));

        var pubBuildingTradingOrders =
                pubBuildingTradingOrderRepository.findAllByAccountIdAndPubBuilding_Id(reqDto.getAccountId(), pubBuilding.getId());

        if (pubBuildingTradingOrders.isEmpty()) {
            pubBuildingTradingOrders =
                    generateOrders(characterDto, cityDto, pubBuilding, configurationDtos, dataConfigurationDtos, systemConfigurationDto);
        }
        return pubBuildingTradingOrders.stream()
                .map(pubBuildingTradingOrder -> PubBuildingTradingOrderConverter.toUiDto(
                        pubBuildingTradingOrder, TradingOrderFacade.buildWithoutExperiences(
                                characterDto, cityDto, pubBuildingTradingOrder, dataConfigurationDtos, configurationDtos,
                                systemConfigurationDto, BUILDING_TYPE)))
                .sorted(Comparator.comparing(BuildingTradingOrderUiDto::getSequenceNumber))
                .toList();
    }

    private List<BuildingTradingOrderUiDto> fetchWithoutCharacter(BuildingTradingOrderFetchedReqDto reqDto, PubBuilding pubBuilding,
            TradingOrderConfigurationDtos configurationDtos) {
        return configurationDtos.stream()
                .map(dto -> PubBuildingTradingOrderConverter.toUiDto(pubBuilding, dto))
                .sorted(Comparator.comparing(BuildingTradingOrderUiDto::getSequenceNumber))
                .toList();
    }

    private List<PubBuildingTradingOrder> generateOrders(
            CharacterDto characterDto, CityDto cityDto, PubBuilding pubBuilding,
            TradingOrderConfigurationDtos configurationDtos, TradingOrderDataConfigurationDtos dataConfigurationDtos,
            SystemConfigurationDto systemConfigurationDto) {
        var cardLevelMap = cardInventoryCommunicationService.fetchCards(dataConfigurationDtos.getSetByCardId()).stream()
                .collect(Collectors.toMap(CardDto::getId, dto -> dto.getMeta().getLevel()));

        return configurationDtos.stream()
                .sorted(Comparator.comparing(TradingOrderConfigurationDto::getSequenceNumber))
                .map(dto -> PubBuildingTradingOrderConverter.toEntity(
                        characterDto.getAccountId(), pubBuilding, TradingOrderFacade.buildWithoutExperiences(
                                characterDto, cityDto, pubBuilding.getLevel(), cardLevelMap, dataConfigurationDtos, dto,
                                systemConfigurationDto, BUILDING_TYPE)))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        pubBuildingTradingOrderRepository::saveAll));
    }

    private void checkOrderRequirements(CharacterDto characterDto, TradingOrderConfigurationDto tradingOrderConfigurationDto) {
        var characterSpecialityDtoMap = characterDto.getSpecialities().stream()
                .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), Function.identity()));

        var speciality = tradingOrderConfigurationDto.getSpeciality();
        var characterSpecialityDto = characterSpecialityDtoMap.get(speciality.getName());
        if (characterSpecialityDto.getLevel() < speciality.getLevel()) {
            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CHARACTER_SPECIALITY_LEVEL,
                    "Character with id = %s does not have a specialty %s with the required level = %s, current level = %s"
                            .formatted(characterDto.getId(), speciality.getName(), speciality.getLevel(),
                                    characterSpecialityDto.getLevel()));
        }
    }

    private TradingOrderFacade reGenerateOrderFacade(
            CharacterDto characterDto, CityDto cityDto, PubBuilding pubBuilding,
            TradingOrderConfigurationDto configurationDto, SystemConfigurationDto systemConfigurationDto) {
        var dataConfigurationDtos = configurationsStorageService.getTradingOrderDataConfigurations();

        var cardLevelMap = cardInventoryCommunicationService.fetchCards(dataConfigurationDtos.getSetByCardId()).stream()
                .collect(Collectors.toMap(CardDto::getId, dto -> dto.getMeta().getLevel()));

        return TradingOrderFacade.buildWithoutExperiences(
                characterDto, cityDto, pubBuilding.getLevel(), cardLevelMap, dataConfigurationDtos, configurationDto,
                systemConfigurationDto, BUILDING_TYPE);
    }

    private void subtractCharacterEnergy(String accountId, Long characterId, Double quantity, AuditAction auditAction,
            Consumer<CharacterEnergyOperationResDto> consumer) {
        if (quantity <= 0) {
            return;
        }
        characterCommunicationService.handleCharacterEnergyOperation(
                CharacterEnergyOperationReqDto.of(
                        accountId, characterId, OperationType.SUBTRACT, quantity,
                        AuditSource.ofBuilding(BUILDING_TYPE), auditAction),
                consumer);
    }

}
