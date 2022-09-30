package io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer;

import static io.fishmaster.ms.be.commons.constant.operation.OperationType.SUBTRACT;
import static io.fishmaster.ms.be.commons.utility.DateTimeUtility.atEndOfWeek;
import static io.fishmaster.ms.be.commons.utility.DateTimeUtility.atStartOfWeek;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility.getCharacterExperiences;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.utility.LockUtility;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.service.AccountBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer.BarmanOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.barman.offer.PubBuildingBarmanOfferHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.PubBuildingBarmanOfferHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation.PubBuildingBarmanOfferHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation.PubBuildingBarmanOfferHistoryGroupedByConfigurationId;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.barman.offer.PubBuildingBarmanOfferHistoryRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer.model.BuildingBarmanOfferProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.PubBuildingBarmanUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.offer.BuildingBarmanOfferUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingBarmanOfferServiceImpl implements PubBuildingBarmanOfferService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final CityCommunicationService cityCommunicationService;
    private final CharacterCommunicationService characterCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;
    private final AccountBalanceCommunicationService accountBalanceCommunicationService;

    private final PubBuildingDailyTaskKafkaSender pubBuildingDailyTaskKafkaSender;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;

    private final PubBuildingBarmanOfferHistoryRepository pubBuildingBarmanOfferHistoryRepository;

    private final LoadingCache<String, ReentrantLock> pubBuildingBarmanOfferExecutedLockRegistry;

    private final Clock utcClock;

    @Override
    public PubBuildingBarmanUiDto getBarman(String accountId, PubBuilding pubBuilding) {
        var uniqueMappedResult = pubBuildingBarmanOfferHistoryRepository.aggregateByAccountIdAndBuildingId(accountId, pubBuilding.getId())
                .getUniqueMappedResult();

        var buildingBarmenOfferUiDtos = fetch(new BuildingBarmanOfferFetchedReqDto(accountId, pubBuilding.getId()));

        return Optional.ofNullable(uniqueMappedResult)
                .map(PubBuildingBarmanOfferHistoryGroupedByBuildingId::getRecord)
                .map(PubBuildingBarmanOfferHistory::getCharacterId)
                .filter(characterId -> characterCommunicationService.existsCharacter(characterId, accountId, pubBuilding.getCityId()))
                .map(characterId -> new PubBuildingBarmanUiDto(Set.of(characterId), buildingBarmenOfferUiDtos))
                .orElse(new PubBuildingBarmanUiDto(Set.of(), buildingBarmenOfferUiDtos));
    }

    @Override
    public List<BuildingBarmanOfferUiDto> fetch(BuildingBarmanOfferFetchedReqDto reqDto) {
        var configurationCountMap = pubBuildingBarmanOfferHistoryRepository.aggregateByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
                reqDto.getAccountId(), reqDto.getBuildingId(), Status.COLLECTED, atStartOfWeek(utcClock), atEndOfWeek(utcClock))
                .getMappedResults().stream()
                .collect(Collectors.toMap(
                        PubBuildingBarmanOfferHistoryGroupedByConfigurationId::getId,
                        PubBuildingBarmanOfferHistoryGroupedByConfigurationId::getCount));

        return configurationsStorageService.getBarmanOfferConfigurations().stream()
                .map(dto -> {
                    var weeklyOffersCount = configurationCountMap.getOrDefault(dto.getId(), 0L);
                    return PubBuildingBarmanOfferHistoryConverter.toUiDto(reqDto.getBuildingId(), dto, weeklyOffersCount);
                })
                .toList();
    }

    @Override
    public BuildingBarmanOfferUiDto executeOffer(BuildingBarmanOfferExecutedReqDto reqDto) {
        return LockUtility.lock(
                () -> {
                    var lockRegistryKey = String.join(
                            "_", reqDto.getAccountId(), String.valueOf(reqDto.getCharacterId()),
                            String.valueOf(reqDto.getBuildingId()), reqDto.getConfigurationId());
                    return pubBuildingBarmanOfferExecutedLockRegistry.getUnchecked(lockRegistryKey);
                },
                () -> {
                    var buildingBarmenOfferProcessExecuted = new BuildingBarmanOfferProcessExecuted(reqDto.getAccountId());
                    try {
                        var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

                        var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

                        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
                        var barmenOfferConfigurationDto = configurationsStorageService.getBarmanOfferConfigurations()
                                .getByIdAndLevelLessThanEqual(reqDto.getConfigurationId(), pubBuilding.getLevel());

                        var weeklyOffersCount = pubBuildingBarmanOfferHistoryRepository
                                .countAllByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(
                                        reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId(), Status.COLLECTED,
                                        atStartOfWeek(utcClock), atEndOfWeek(utcClock));

                        var newWeeklyOffersCount = Math.max(barmenOfferConfigurationDto.getWeeklyLimit() - weeklyOffersCount, 0);

                        if (newWeeklyOffersCount <= 0) {
                            throw new ServiceException(
                                    ExceptionCode.THE_LIMIT_IS_OVER,
                                    "The one-week limit for the offer with id = %s has expired".formatted(reqDto.getConfigurationId()));
                        }

                        var characterDto = characterCommunicationService.lockCharacterStatus(
                                reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), Status.FREE,
                                CharacterParamDto.of(false, false, false, true),
                                buildingBarmenOfferProcessExecuted::setCharacterStatus);

                        checkRequirements(characterDto, barmenOfferConfigurationDto.getRequired());

                        subtractCostFromAccountBalance(
                                reqDto, barmenOfferConfigurationDto.getRequired().getCost(),
                                buildingBarmenOfferProcessExecuted);

                        createCards(reqDto, barmenOfferConfigurationDto.getResult().getCard(), buildingBarmenOfferProcessExecuted);

                        var pubBuildingBarmenOfferHistory = moveToHistory(
                                reqDto, characterDto, barmenOfferConfigurationDto, systemConfigurationDto,
                                buildingBarmenOfferProcessExecuted);

                        buildingBarmenOfferProcessExecuted.updateCards(Status.ACTIVE, cityDto.getId());
                        buildingBarmenOfferProcessExecuted.updateCharacterStatus(Status.FREE);

                        characterCommunicationService.addExperiences(
                                pubBuildingBarmenOfferHistory.getAccountId(), pubBuildingBarmenOfferHistory.getCharacterId(),
                                pubBuildingBarmenOfferHistory.getResult().getExperiences(), AuditSource.PUB, AuditAction.OFFER_EXECUTE);

                        buildingBarmenOfferProcessExecuted.complete(
                                characterCommunicationService::completeCharacterStatus,
                                cardInventoryCommunicationService::completeAccountCards);

                        pubBuildingDailyTaskKafkaSender.refreshCardTaskProgress(
                                TaskGoalType.PURCHASED_CARD, buildingBarmenOfferProcessExecuted.getAccountId(), characterDto.getId(),
                                cityDto.getId(),
                                BuildingType.PUB, buildingBarmenOfferProcessExecuted.getCards());

                        return PubBuildingBarmanOfferHistoryConverter.toUiDto(
                                pubBuildingBarmenOfferHistory, barmenOfferConfigurationDto, weeklyOffersCount + 1);
                    } catch (Exception e) {
                        log.error("Error while on execute barmen offer in pub building. {}. Error = {}", reqDto, e.getMessage());

                        buildingBarmenOfferProcessExecuted.rollback(
                                accountBalanceCommunicationService::handleOperation,
                                cardInventoryCommunicationService::rollbackAccountCards,
                                characterCommunicationService::rollbackCharacterStatus);

                        throw e;
                    }
                });
    }

    private void checkRequirements(CharacterDto characterDto, BarmanOfferConfigurationDto.Required required) {
        var characterSpecialityDtoMap = characterDto.getSpecialities().stream()
                .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), Function.identity()));

        required.getSpecialities().forEach(speciality -> {
            var characterSpecialityDto = characterSpecialityDtoMap.get(speciality.getName());
            if (Objects.isNull(characterSpecialityDto) || characterSpecialityDto.getLevel() < speciality.getLevel()) {
                throw new ServiceException(
                        ExceptionCode.NOT_ENOUGH_CHARACTER_SPECIALITY_LEVEL,
                        ("Character with id = %s does not have a specialty %s with the required level = %s, " +
                                "current %s with level = %s").formatted(characterDto.getId(), speciality.getName(),
                                        speciality.getLevel(), characterSpecialityDto.getConfiguration().getSpeciality(),
                                        characterSpecialityDto.getLevel()));
            }
        });
    }

    private void subtractCostFromAccountBalance(
            BuildingBarmanOfferExecutedReqDto reqDto, BarmanOfferConfigurationDto.Cost requiredCost,
            BuildingBarmanOfferProcessExecuted buildingBarmanOfferProcessExecuted) {
        if (requiredCost.getQuantity() <= 0) {
            return;
        }
        accountBalanceCommunicationService.handleOperation(
                AccountBalanceOperationReqDto.of(
                        reqDto.getAccountId(), SUBTRACT, requiredCost.getCurrency(), requiredCost.getQuantity(),
                        AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.OFFER_EXECUTE),
                buildingBarmanOfferProcessExecuted::setAccountBalanceOperationReq);
    }

    private void createCards(
            BuildingBarmanOfferExecutedReqDto reqDto, BarmanOfferConfigurationDto.Card card,
            BuildingBarmanOfferProcessExecuted buildingBarmanOfferProcessExecuted) {
        var accountCardDtos = IntStream.range(0, card.getQuantity())
                .mapToObj(value -> AccountCardDto.of(reqDto.getAccountId(), card.getId(), CardSource.PUB_BARMEN_OFFER))
                .toList();

        cardInventoryCommunicationService.createAccountCards(accountCardDtos, buildingBarmanOfferProcessExecuted::setCards);
    }

    private PubBuildingBarmanOfferHistory moveToHistory(
            BuildingBarmanOfferExecutedReqDto reqDto, CharacterDto characterDto,
            BarmanOfferConfigurationDto barmanOfferConfigurationDto, SystemConfigurationDto systemConfigurationDto,
            BuildingBarmanOfferProcessExecuted buildingBarmanOfferProcessExecuted) {
        var characterExperiences = getCharacterExperiences(
                characterDto, barmanOfferConfigurationDto, systemConfigurationDto, BUILDING_TYPE);
        return pubBuildingBarmanOfferHistoryRepository.save(
                PubBuildingBarmanOfferHistoryConverter.toEntity(
                        reqDto, Status.COLLECTED, characterExperiences, buildingBarmanOfferProcessExecuted.getCards(), utcClock));
    }

}
