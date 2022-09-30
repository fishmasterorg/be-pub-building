package io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge;

import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.commons.constant.Currency;
import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.operation.OperationType;
import io.fishmaster.ms.be.commons.dto.PageUiDto;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.service.AccountBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.account.self.service.AccountCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.service.CityBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.trading.challenge.PubBuildingTradingChallengeConverter;
import io.fishmaster.ms.be.pub.building.converter.trading.challenge.PubBuildingTradingChallengeHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.trading.challenge.PubBuildingTradingChallengeRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.PubBuildingTradingChallengeHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.aggregation.PubBuildingTradingChallengeHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.trading.challenge.PubBuildingTradingChallengeHistoryRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.trading.challenge.PubBuildingTradingChallengeKafkaSender;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.model.BuildingTradingChallengeProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.utility.TradingChallengeFacade;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeHistoryUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.PubBuildingTradingChallengeUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingTradingChallengeServiceImpl implements PubBuildingTradingChallengeService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final AccountCommunicationService accountCommunicationService;
    private final CharacterCommunicationService characterCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;
    private final AccountBalanceCommunicationService accountBalanceCommunicationService;
    private final CityBalanceCommunicationService cityBalanceCommunicationService;
    private final CityCommunicationService cityCommunicationService;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingTradingChallengeKafkaSender pubBuildingTradingChallengeKafkaSender;

    private final PubBuildingRepository pubBuildingRepository;
    private final PubBuildingTradingChallengeRepository pubBuildingTradingChallengeRepository;
    private final PubBuildingTradingChallengeHistoryRepository pubBuildingTradingChallengeHistoryRepository;

    private final Clock utcClock;

    @Override
    public PubBuildingTradingChallengeUiDto getTradingChallenge(String accountId, PubBuilding pubBuilding) {
        var uniqueMappedResult =
                pubBuildingTradingChallengeHistoryRepository.aggregateByAccountIdAndBuildingId(accountId, pubBuilding.getId())
                        .getUniqueMappedResult();

        return Optional.ofNullable(uniqueMappedResult)
                .map(PubBuildingTradingChallengeHistoryGroupedByBuildingId::getRecord)
                .map(PubBuildingTradingChallengeHistory::getCharacterId)
                .filter(characterId -> characterCommunicationService.existsCharacter(characterId, accountId, pubBuilding.getCityId()))
                .map(characterId -> new PubBuildingTradingChallengeUiDto(Set.of(characterId)))
                .orElse(new PubBuildingTradingChallengeUiDto(Set.of()));
    }

    @Override
    public BuildingTradingChallengeUiDto fetchActive(BuildingTradingChallengeFetchedReqDto reqDto) {
        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();

        var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

        var cityTaxRateDto = cityCommunicationService.fetchCity(pubBuilding.getCityId()).getCityTaxRate();

        var pubBuildingFoodChallenge = pubBuildingTradingChallengeRepository.findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE, "Has not active challenge in pub."));

        var configurationDtos = configurationsStorageService.getTradingChallengeConfigurations();
        var dataConfigurationDtos = configurationsStorageService.getTradingChallengeDataConfigurations();

        return PubBuildingTradingChallengeConverter.toUiDto(
                pubBuildingFoodChallenge.getId(),
                TradingChallengeFacade.buildWithoutExperiences(
                        systemConfigurationDto.getTaxRate(), cityTaxRateDto.getPubChallenge(), pubBuildingFoodChallenge,
                        dataConfigurationDtos, configurationDtos, utcClock));
    }

    @Override
    public PageUiDto<BuildingTradingChallengeHistoryUiDto> fetchHistory(Integer page, Integer size) {
        var createdDateSort = Sort.sort(PubBuildingTradingChallengeHistory.class)
                .by(PubBuildingTradingChallengeHistory::getCreatedDate)
                .descending();

        var pageRequest = PageRequest.of(page, size, createdDateSort);
        var historyPage = pubBuildingTradingChallengeHistoryRepository.findAll(pageRequest);
        return PageUiDto.of(
                historyPage.getContent(), historyPage.getTotalPages(), historyPage.getTotalElements(),
                PubBuildingTradingChallengeHistoryConverter::toUiDto);
    }

    @Transactional
    @Override
    public BuildingTradingChallengeHistoryUiDto executeChallenge(BuildingTradingChallengeExecutedReqDto reqDto) {
        var buildingTradingChallengeProcessExecuted = new BuildingTradingChallengeProcessExecuted(reqDto.getAccountId());
        try {
            var oldPubBuildingTradingChallenge =
                    pubBuildingTradingChallengeRepository.findWithLockById(reqDto.getBuildingTradingChallengeId())
                            .orElseThrow(() -> new ServiceException(
                                    ExceptionCode.INNER_SERVICE,
                                    "Pub building Trading challenge with id = %s not exists"
                                            .formatted(reqDto.getBuildingTradingChallengeId())));

            var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

            var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

            var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();
            var dataConfigurationDtos = configurationsStorageService.getTradingChallengeDataConfigurations();
            var configurationDto = configurationsStorageService.getTradingChallengeConfigurations()
                    .getById(oldPubBuildingTradingChallenge.getConfigurationId());

            var characterDto = characterCommunicationService.lockCharacterStatus(
                    reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), Status.FREE,
                    CharacterParamDto.of(false, true, true, true),
                    buildingTradingChallengeProcessExecuted::setCharacterStatus);

            var accountDto = accountCommunicationService.getById(reqDto.getAccountId());

            var oldTradingChallengeFacade = TradingChallengeFacade.buildWithExperiences(
                    characterDto, cityDto.getCityTaxRate().getPubChallenge(), oldPubBuildingTradingChallenge, dataConfigurationDtos,
                    configurationDto, systemConfigurationDto, BUILDING_TYPE, utcClock);

            checkChallengeRequirements(characterDto, configurationDto);

            cardInventoryCommunicationService.lockAccountCards(
                    AccountCardLockedReqDto.of(
                            buildingTradingChallengeProcessExecuted.getAccountId(), cityDto.getId(), oldTradingChallengeFacade.getCardId(),
                            oldPubBuildingTradingChallenge.getData().getCardsQuantity(), CardType.FOOD, Status.ACTIVE),
                    oldPubBuildingTradingChallenge.getData().getCardsQuantity(), buildingTradingChallengeProcessExecuted::setLockedCards);

            cityBalanceCommunicationService.handleOperation(
                    CityBalanceOperationReqDto.of(
                            cityDto.getId(), OperationType.ADD, Currency.GOLD, oldTradingChallengeFacade.getTax(),
                            AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.CHALLENGE_EXECUTE),
                    buildingTradingChallengeProcessExecuted::setCityBalanceOperationReq);

            accountBalanceCommunicationService.handleOperation(
                    AccountBalanceOperationReqDto.of(
                            reqDto.getAccountId(), OperationType.ADD, Currency.GOLD, oldTradingChallengeFacade.getCost(),
                            AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.CHALLENGE_EXECUTE),
                    buildingTradingChallengeProcessExecuted::setAccountBalanceOperationReq);

            pubBuildingTradingChallengeRepository.delete(oldPubBuildingTradingChallenge);

            var pubBuildingTradingChallengeHistory = pubBuildingTradingChallengeHistoryRepository.save(
                    PubBuildingTradingChallengeHistoryConverter.toEntityWithStatusDone(
                            accountDto, characterDto, pubBuilding, oldPubBuildingTradingChallenge,
                            oldTradingChallengeFacade, buildingTradingChallengeProcessExecuted.getAccountBalanceOperationReq(), utcClock));

            pubBuildingTradingChallengeRepository.save(
                    PubBuildingTradingChallengeConverter.toEntity(
                            TradingChallengeFacade.buildWithoutExperiences(dataConfigurationDtos, configurationDto, utcClock)));

            characterCommunicationService.addExperiences(
                    reqDto.getAccountId(), reqDto.getCharacterId(), oldTradingChallengeFacade.getExperiences(),
                    AuditSource.ofBuilding(BUILDING_TYPE), AuditAction.CHALLENGE_EXECUTE);

            buildingTradingChallengeProcessExecuted.updateLockedCards(Status.USED, CardSource.PUB_TRADING_CHALLENGE);
            buildingTradingChallengeProcessExecuted.updateCharacterStatus(Status.FREE);

            buildingTradingChallengeProcessExecuted.complete(
                    cardInventoryCommunicationService::completeAccountCards,
                    characterCommunicationService::completeCharacterStatus);

            pubBuildingTradingChallengeKafkaSender.refresh();

            return PubBuildingTradingChallengeHistoryConverter.toUiDto(pubBuildingTradingChallengeHistory);
        } catch (Exception e) {
            log.error("Error while on execute building trading challenge. {}. Error = {}", reqDto, e.getMessage());

            buildingTradingChallengeProcessExecuted.rollback(
                    cityBalanceCommunicationService::handleOperation,
                    accountBalanceCommunicationService::handleOperation,
                    cardInventoryCommunicationService::rollbackAccountCards,
                    characterCommunicationService::rollbackCharacterStatus);

            throw e;
        }
    }

    @Transactional
    @Override
    public void moveChallengeToHistoryWhenExpire() {
        var pubBuildingTradingChallengeOptional =
                pubBuildingTradingChallengeRepository.findWithLockByChallengeEndedTimeLessThanEqual(utcClock.millis());
        if (pubBuildingTradingChallengeOptional.isEmpty()) {
            return;
        }
        var oldPubBuildingTradingChallenge = pubBuildingTradingChallengeOptional.get();

        var dataConfigurationDtos = configurationsStorageService.getTradingChallengeDataConfigurations();
        var configurationDtos = configurationsStorageService.getTradingChallengeConfigurations();

        var oldTradingChallengeFacade = TradingChallengeFacade.buildWithoutExperiences(
                0D, 0D, oldPubBuildingTradingChallenge, dataConfigurationDtos,
                configurationDtos, utcClock);

        pubBuildingTradingChallengeRepository.delete(oldPubBuildingTradingChallenge);

        pubBuildingTradingChallengeHistoryRepository.save(
                PubBuildingTradingChallengeHistoryConverter.toEntityWithStatusFailed(
                        oldPubBuildingTradingChallenge.getId(), oldTradingChallengeFacade, utcClock));

        pubBuildingTradingChallengeRepository.save(
                PubBuildingTradingChallengeConverter.toEntity(
                        TradingChallengeFacade.buildWithoutExperiences(
                                dataConfigurationDtos, oldTradingChallengeFacade.getConfigurationDto(), utcClock)));

        pubBuildingTradingChallengeKafkaSender.refresh();
    }

    private void checkChallengeRequirements(CharacterDto characterDto, TradingChallengeConfigurationDto configurationDto) {
        var characterSpecialityDtoMap = characterDto.getSpecialities().stream()
                .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), Function.identity()));

        var speciality = configurationDto.getSpeciality();
        var characterSpecialityDto = characterSpecialityDtoMap.get(speciality.getName());
        if (characterSpecialityDto.getLevel() < speciality.getLevel()) {
            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CHARACTER_SPECIALITY_LEVEL,
                    "Character with id = %s does not have a specialty %s with the required level = %s, current level = %s"
                            .formatted(characterDto.getId(), speciality.getName(), speciality.getLevel(),
                                    characterSpecialityDto.getLevel()));
        }
    }

}
