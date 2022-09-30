package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer;

import static io.fishmaster.ms.be.commons.constant.operation.OperationType.SUBTRACT;
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

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.utility.LockUtility;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.service.AccountBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.ExchangerOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.group.ExchangerOfferGroupConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.exchanger.offer.PubBuildingExchangerOfferHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.PubBuildingExchangerOfferHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation.PubBuildingExchangerOfferHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation.PubBuildingExchangerOfferHistoryGroupedByConfigurationId;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.exchanger.offer.PubBuildingExchangerOfferHistoryRepository;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer.model.BuildingExchangerOfferProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferGroupUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.PubBuildingExchangerOfferUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingExchangerOfferServiceImpl implements PubBuildingExchangerOfferService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final CharacterCommunicationService characterCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;
    private final AccountBalanceCommunicationService accountBalanceCommunicationService;
    private final CityCommunicationService cityCommunicationService;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;

    private final PubBuildingExchangerOfferHistoryRepository pubBuildingExchangerOfferHistoryRepository;

    private final LoadingCache<String, ReentrantLock> pubBuildingExchangerOfferExecutedLockRegistry;

    private final Clock utcClock;

    @Override
    public PubBuildingExchangerOfferUiDto getExchangerOffer(String accountId, PubBuilding pubBuilding) {
        var uniqueMappedResult =
                pubBuildingExchangerOfferHistoryRepository.aggregateByAccountIdAndBuildingId(accountId, pubBuilding.getId())
                        .getUniqueMappedResult();

        var lastCharacterIdUsed = Optional.ofNullable(uniqueMappedResult)
                .map(PubBuildingExchangerOfferHistoryGroupedByBuildingId::getRecord)
                .map(PubBuildingExchangerOfferHistory::getCharacterId)
                .filter(characterId -> characterCommunicationService.existsCharacter(characterId, accountId, pubBuilding.getCityId()))
                .orElse(null);

        var buildingExchangerOfferGroupUiDtos =
                fetch(new BuildingExchangerOfferFetchedReqDto(accountId, lastCharacterIdUsed, pubBuilding.getId()));

        var lastCharacterIdsUsed = Objects.isNull(lastCharacterIdUsed) ? Set.<Long>of() : Set.of(lastCharacterIdUsed);

        return new PubBuildingExchangerOfferUiDto(lastCharacterIdsUsed, buildingExchangerOfferGroupUiDtos);
    }

    @Override
    public List<BuildingExchangerOfferGroupUiDto> fetch(BuildingExchangerOfferFetchedReqDto reqDto) {
        var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

        var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

        var characterDto = getCharacter(reqDto, cityDto.getId());

        var offerGroupConfigurationDtos = configurationsStorageService.getExchangerOfferGroupConfigurations()
                .getListByDate(utcClock.millis());

        var offerCountMap = offerGroupConfigurationDtos.stream()
                .map(ExchangerOfferGroupConfigurationDto::getDate)
                .map(date -> Pair.of(date.getStart(), date.getEnd()))
                .distinct()
                .map(pair -> pubBuildingExchangerOfferHistoryRepository.aggregateByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
                        reqDto.getAccountId(), reqDto.getBuildingId(), Status.COLLECTED, pair.getLeft(), pair.getRight()))
                .map(AggregationResults::getMappedResults)
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        PubBuildingExchangerOfferHistoryGroupedByConfigurationId::getId,
                        PubBuildingExchangerOfferHistoryGroupedByConfigurationId::getCount));

        var offerConfigurationDtos = configurationsStorageService.getExchangerOfferConfigurations();

        return offerGroupConfigurationDtos.stream()
                .map(groupDto -> {
                    var buildingExchangerOfferUiDtos = offerConfigurationDtos.getListByIdIn(groupDto.getOfferIds(), dto -> {
                        var offersCount = offerCountMap.getOrDefault(dto.getId(), 0L);
                        return PubBuildingExchangerOfferHistoryConverter.toUiDto(reqDto.getBuildingId(), dto, offersCount, characterDto);
                    });
                    return PubBuildingExchangerOfferHistoryConverter.toGroupUiDto(groupDto, buildingExchangerOfferUiDtos);
                })
                .sorted()
                .toList();
    }

    @Override
    public BuildingExchangerOfferUiDto executeOffer(BuildingExchangerOfferExecutedReqDto reqDto) {
        return LockUtility.lock(
                () -> {
                    var lockRegistryKey = String.join(
                            "_", reqDto.getAccountId(), String.valueOf(reqDto.getCharacterId()),
                            String.valueOf(reqDto.getBuildingId()), reqDto.getConfigurationId());
                    return pubBuildingExchangerOfferExecutedLockRegistry.getUnchecked(lockRegistryKey);
                },
                () -> {
                    var buildingExchangerOfferProcessExecuted = new BuildingExchangerOfferProcessExecuted(reqDto.getAccountId());
                    try {
                        var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

                        var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

                        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
                        var exchangerOfferGroupConfigurationDto = configurationsStorageService.getExchangerOfferGroupConfigurations()
                                .getByDateAndOfferId(utcClock.millis(), reqDto.getConfigurationId());
                        var exchangerOfferConfigurationDto = configurationsStorageService.getExchangerOfferConfigurations()
                                .getByIdAndLevelLessThanEqual(reqDto.getConfigurationId(), pubBuilding.getLevel());

                        var offersCount = pubBuildingExchangerOfferHistoryRepository
                                .countAllByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(
                                        reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId(), Status.COLLECTED,
                                        exchangerOfferGroupConfigurationDto.getDate().getStart(),
                                        exchangerOfferGroupConfigurationDto.getDate().getEnd());

                        var newOffersCount = Math.max(exchangerOfferConfigurationDto.getLimit() - offersCount, 0);

                        if (newOffersCount <= 0) {
                            throw new ServiceException(
                                    ExceptionCode.THE_LIMIT_IS_OVER,
                                    "The limit for the offer with id = %s has expired".formatted(reqDto.getConfigurationId()));
                        }

                        var characterDto = characterCommunicationService.lockCharacterStatus(
                                reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), Status.FREE,
                                CharacterParamDto.of(false, true, true, true),
                                buildingExchangerOfferProcessExecuted::setCharacterStatus);

                        checkRequirements(characterDto, exchangerOfferConfigurationDto.getRequired());

                        lockCards(reqDto, cityDto, exchangerOfferConfigurationDto.getRequired().getCards(),
                                buildingExchangerOfferProcessExecuted);

                        subtractEnergyFromCharacter(
                                reqDto, characterDto, exchangerOfferConfigurationDto.getRequired().getEnergy(),
                                buildingExchangerOfferProcessExecuted);

                        subtractCostFromAccountBalance(
                                reqDto, exchangerOfferConfigurationDto.getRequired().getCost(),
                                buildingExchangerOfferProcessExecuted);

                        createCards(reqDto, exchangerOfferConfigurationDto.getResult().getCard(),
                                buildingExchangerOfferProcessExecuted);

                        var pubBuildingExchangerOfferHistory = moveToHistory(
                                reqDto, characterDto, exchangerOfferConfigurationDto, systemConfigurationDto,
                                buildingExchangerOfferProcessExecuted);

                        buildingExchangerOfferProcessExecuted.updateLockedCards(Status.USED, CardSource.PUB_EXCHANGER_OFFER);
                        buildingExchangerOfferProcessExecuted.updateCreatedCards(Status.ACTIVE, cityDto.getId());
                        buildingExchangerOfferProcessExecuted.updateCharacterStatus(Status.FREE);

                        characterDto = characterCommunicationService.addExperiences(
                                pubBuildingExchangerOfferHistory.getAccountId(), pubBuildingExchangerOfferHistory.getCharacterId(),
                                pubBuildingExchangerOfferHistory.getResult().getExperiences(), AuditSource.PUB,
                                AuditAction.OFFER_EXECUTE);

                        buildingExchangerOfferProcessExecuted.complete(
                                characterCommunicationService::completeCharacterStatus,
                                cardInventoryCommunicationService::completeAccountCards);

                        return PubBuildingExchangerOfferHistoryConverter.toUiDto(
                                pubBuildingExchangerOfferHistory, exchangerOfferConfigurationDto, offersCount + 1, characterDto);
                    } catch (Exception e) {
                        log.error("Error while on execute exchanger offer in pub building. {}. Error = {}", reqDto,
                                e.getMessage());

                        buildingExchangerOfferProcessExecuted.rollback(
                                accountBalanceCommunicationService::handleOperation,
                                characterCommunicationService::handleCharacterEnergyOperation,
                                cardInventoryCommunicationService::rollbackAccountCards,
                                characterCommunicationService::rollbackCharacterStatus);

                        throw e;
                    }
                });
    }

    private CharacterDto getCharacter(BuildingExchangerOfferFetchedReqDto reqDto, Long cityId) {
        if (Objects.isNull(reqDto.getCharacterId())) {
            return CharacterDto.ofDefault();
        }
        return characterCommunicationService.fetchCharacter(
                reqDto.getCharacterId(), reqDto.getAccountId(), cityId,
                CharacterParamDto.of(false, true, true, true));
    }

    private void checkRequirements(CharacterDto characterDto, ExchangerOfferConfigurationDto.Required required) {
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

    private void lockCards(
            BuildingExchangerOfferExecutedReqDto reqDto, CityDto cityDto, List<ExchangerOfferConfigurationDto.Card> requiredCards,
            BuildingExchangerOfferProcessExecuted buildingExchangerOfferProcessExecuted) {
        var toLockedCards = requiredCards.stream()
                .map(card -> AccountCardLockedForCraftReqDto.of(
                        reqDto.getAccountId(), cityDto.getId(), Status.ACTIVE, card.getId(), card.getQuantity()))
                .toList();

        var totalCount = toLockedCards.stream().map(AccountCardLockedForCraftReqDto::getLimit).reduce(0, Integer::sum);

        cardInventoryCommunicationService.lockForCraftAccountCards(
                toLockedCards, totalCount, buildingExchangerOfferProcessExecuted::setLockedCards);
    }

    private void subtractEnergyFromCharacter(
            BuildingExchangerOfferExecutedReqDto reqDto, CharacterDto characterDto, Double requiredEnergy,
            BuildingExchangerOfferProcessExecuted buildingExchangerOfferProcessExecuted) {
        requiredEnergy = CharacterInfluenceUtility.getCharacterEnergy(characterDto, requiredEnergy, BUILDING_TYPE);
        if (requiredEnergy <= 0) {
            return;
        }
        characterCommunicationService.handleCharacterEnergyOperation(
                CharacterEnergyOperationReqDto.of(
                        reqDto.getAccountId(), reqDto.getCharacterId(), SUBTRACT, requiredEnergy,
                        AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.OFFER_EXECUTE),
                buildingExchangerOfferProcessExecuted::setCharacterEnergyOperationRes);
    }

    private void subtractCostFromAccountBalance(
            BuildingExchangerOfferExecutedReqDto reqDto, ExchangerOfferConfigurationDto.Cost requiredCost,
            BuildingExchangerOfferProcessExecuted buildingExchangerOfferProcessExecuted) {
        if (requiredCost.getQuantity() <= 0) {
            return;
        }
        accountBalanceCommunicationService.handleOperation(
                AccountBalanceOperationReqDto.of(
                        reqDto.getAccountId(), SUBTRACT, requiredCost.getCurrency(), requiredCost.getQuantity(),
                        AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.OFFER_EXECUTE),
                buildingExchangerOfferProcessExecuted::setAccountBalanceOperationReq);
    }

    private void createCards(
            BuildingExchangerOfferExecutedReqDto reqDto, ExchangerOfferConfigurationDto.Card card,
            BuildingExchangerOfferProcessExecuted buildingExchangerOfferProcessExecuted) {
        var accountCardDtos = IntStream.range(0, card.getQuantity())
                .mapToObj(value -> AccountCardDto.of(reqDto.getAccountId(), card.getId(), CardSource.PUB_EXCHANGER_OFFER))
                .toList();

        cardInventoryCommunicationService.createAccountCards(accountCardDtos, buildingExchangerOfferProcessExecuted::setCreatedCards);
    }

    private PubBuildingExchangerOfferHistory moveToHistory(
            BuildingExchangerOfferExecutedReqDto reqDto, CharacterDto characterDto,
            ExchangerOfferConfigurationDto exchangerOfferConfigurationDto, SystemConfigurationDto systemConfigurationDto,
            BuildingExchangerOfferProcessExecuted buildingExchangerOfferProcessExecuted) {
        var characterExperiences = getCharacterExperiences(
                characterDto, exchangerOfferConfigurationDto, systemConfigurationDto, BUILDING_TYPE);

        return pubBuildingExchangerOfferHistoryRepository.save(
                PubBuildingExchangerOfferHistoryConverter.toEntity(
                        reqDto, Status.COLLECTED, characterExperiences, buildingExchangerOfferProcessExecuted.getLockedCards(),
                        buildingExchangerOfferProcessExecuted.getCreatedCards(), utcClock));
    }

}
