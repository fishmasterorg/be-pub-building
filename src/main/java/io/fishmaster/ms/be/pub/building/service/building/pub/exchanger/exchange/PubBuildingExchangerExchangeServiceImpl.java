package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange;

import static io.fishmaster.ms.be.commons.constant.Status.ACTIVE;
import static io.fishmaster.ms.be.commons.constant.Status.FREE;
import static io.fishmaster.ms.be.commons.constant.Status.USED;
import static io.fishmaster.ms.be.commons.constant.audit.AuditAction.EXCHANGE_EXECUTE;
import static io.fishmaster.ms.be.commons.constant.audit.AuditAction.TAX_FOR_EXCHANGE_EXECUTE;
import static io.fishmaster.ms.be.commons.constant.operation.OperationType.ADD;
import static io.fishmaster.ms.be.commons.constant.operation.OperationType.SUBTRACT;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;

import io.fishmaster.ms.be.commons.constant.Currency;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.utility.LockUtility;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.service.AccountBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.service.CityBalanceCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.exchanger.exchange.PubBuildingExchangerExchangeHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.PubBuildingExchangerExchangeHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.aggregation.PubBuildingEquipmentExchangeHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.exchanger.exchange.PubBuildingExchangerExchangeHistoryRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.model.BuildingEquipmentExchangeProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.BuildingExchangerExchangeUtilityService;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.model.BuildingExchangerExchangeData;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.BuildingExchangerExchangeUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.PubBuildingExchangerExchangeUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingExchangerExchangeServiceImpl implements PubBuildingExchangerExchangeService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final CardInventoryCommunicationService cardInventoryCommunicationService;
    private final CharacterCommunicationService characterCommunicationService;
    private final AccountBalanceCommunicationService accountBalanceCommunicationService;
    private final CityBalanceCommunicationService cityBalanceCommunicationService;
    private final CityCommunicationService cityCommunicationService;

    private final PubBuildingDailyTaskKafkaSender pubBuildingDailyTaskKafkaSender;

    private final ConfigurationsStorageService configurationsStorageService;

    private final BuildingExchangerExchangeUtilityService buildingExchangeUtilityService;

    private final PubBuildingRepository pubBuildingRepository;
    private final PubBuildingExchangerExchangeHistoryRepository pubBuildingEquipmentExchangeHistoryRepository;

    private final LoadingCache<String, ReentrantLock> pubBuildingExchangerExchangeExecutedLockRegistry;

    private final Clock utcClock;

    @Override
    public PubBuildingExchangerExchangeUiDto getExchangerExchange(String accountId, PubBuilding pubBuilding) {
        var uniqueMappedResult =
                pubBuildingEquipmentExchangeHistoryRepository.aggregateByAccountIdAndBuildingId(accountId, pubBuilding.getId())
                        .getUniqueMappedResult();

        return Optional.ofNullable(uniqueMappedResult)
                .map(PubBuildingEquipmentExchangeHistoryGroupedByBuildingId::getRecord)
                .map(PubBuildingExchangerExchangeHistory::getCharacterId)
                .filter(characterId -> characterCommunicationService.existsCharacter(characterId, accountId, pubBuilding.getCityId()))
                .map(characterId -> new PubBuildingExchangerExchangeUiDto(Set.of(characterId)))
                .orElse(new PubBuildingExchangerExchangeUiDto(Set.of()));
    }

    @Override
    public BuildingExchangerExchangeUiDto executeExchange(BuildingExchangerExchangeExecutedReqDto reqDto) {
        return LockUtility.lock(
                () -> {
                    var lockRegistryKey = String.join(
                            "_", reqDto.getAccountId(), String.valueOf(reqDto.getCharacterId()),
                            String.valueOf(reqDto.getBuildingId()), reqDto.getRecipeConfigurationId());
                    return pubBuildingExchangerExchangeExecutedLockRegistry.getUnchecked(lockRegistryKey);
                },
                () -> {
                    var buildingEquipmentExchangeProcessExecuted = new BuildingEquipmentExchangeProcessExecuted(reqDto.getAccountId());
                    try {
                        var building = pubBuildingRepository.getById(reqDto.getBuildingId());

                        var cityDto = cityCommunicationService.fetchCity(building.getCityId());

                        var recipeConfigurationDto = configurationsStorageService.getExchangerExchangeRecipeConfigurations()
                                .getByIdAndLevelLessThanEqual(reqDto.getRecipeConfigurationId(), building.getLevel());

                        var characterDto = characterCommunicationService.lockCharacterStatus(
                                CharacterStatusReqDto.of(reqDto.getCharacterId(), reqDto.getAccountId(), cityDto.getId(), FREE),
                                CharacterParamDto.of(true, true, true, true),
                                buildingEquipmentExchangeProcessExecuted::setCharacterStatus);

                        checkRequirements(characterDto, recipeConfigurationDto.getRequired());

                        var buildingExchangeData = buildingExchangeUtilityService.getBuildingExchangerExchangeData(
                                reqDto, cityDto, characterDto, recipeConfigurationDto, BUILDING_TYPE);

                        lockCards(buildingExchangeData.getLockedCards(), buildingEquipmentExchangeProcessExecuted);

                        cardInventoryCommunicationService.createAccountCards(
                                buildingExchangeData.getAllCraftedCards(), buildingEquipmentExchangeProcessExecuted::setCraftedCards);

                        updateCharacterEnergyAndAccountAndCityBalances(reqDto, cityDto, buildingExchangeData,
                                buildingEquipmentExchangeProcessExecuted);

                        buildingEquipmentExchangeProcessExecuted.updateLockedCards(USED, CardSource.PUB_EXCHANGER_EXCHANGE);
                        buildingEquipmentExchangeProcessExecuted.updateCraftedCards(ACTIVE, cityDto.getId());
                        buildingEquipmentExchangeProcessExecuted.updateCharacterStatus(FREE);

                        var pubBuildingEquipmentExchangeHistory = pubBuildingEquipmentExchangeHistoryRepository.save(
                                PubBuildingExchangerExchangeHistoryConverter.toEntityWithStatusDone(
                                        reqDto, recipeConfigurationDto, buildingExchangeData, buildingEquipmentExchangeProcessExecuted,
                                        utcClock));

                        characterCommunicationService.addExperiences(
                                pubBuildingEquipmentExchangeHistory.getAccountId(), pubBuildingEquipmentExchangeHistory.getCharacterId(),
                                buildingExchangeData.getExperiencesList(), AuditSource.ofBuilding(BUILDING_TYPE),
                                EXCHANGE_EXECUTE);

                        buildingEquipmentExchangeProcessExecuted.complete(
                                cardInventoryCommunicationService::completeAccountCards,
                                characterCommunicationService::completeCharacterStatus);

                        pubBuildingDailyTaskKafkaSender.refreshEnergyTaskProgress(
                                TaskGoalType.SPENT_ENERGY, buildingEquipmentExchangeProcessExecuted.getAccountId(), characterDto.getId(),
                                cityDto.getId(), BUILDING_TYPE, buildingExchangeData.getCharacterEnergy().intValue());

                        return PubBuildingExchangerExchangeHistoryConverter.toUiDto(pubBuildingEquipmentExchangeHistory);
                    } catch (Exception e) {
                        log.error("Error while on execute building equipment exchange. {}. Error = {}", reqDto, e.getMessage());

                        buildingEquipmentExchangeProcessExecuted.rollback(
                                characterCommunicationService::rollbackCharacterStatus,
                                cardInventoryCommunicationService::rollbackAccountCards,
                                cityBalanceCommunicationService::handleOperation,
                                accountBalanceCommunicationService::handleOperation,
                                characterCommunicationService::handleCharacterEnergyOperation);

                        throw e;
                    }
                });
    }

    private void checkRequirements(CharacterDto characterDto, ExchangerExchangeRecipeConfigurationDto.Required required) {
        if (required.getEnergy() > characterDto.getEnergy().getQuantity()) {
            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CHARACTER_ENERGY,
                    "Character with id = %s not enough energy quantity = %s".formatted(
                            characterDto.getId(), required.getEnergy()));
        }

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
            List<AccountCardLockedForCraftReqDto> toLockedCards,
            BuildingEquipmentExchangeProcessExecuted buildingEquipmentExchangeProcessExecuted) {
        var match = toLockedCards.stream().allMatch(reqDto -> reqDto.getLimit() == 0);
        if (match) {
            return;
        }
        var totalCount = toLockedCards.stream().map(AccountCardLockedForCraftReqDto::getLimit).reduce(0, Integer::sum);

        cardInventoryCommunicationService.lockForCraftAccountCards(
                toLockedCards, totalCount, buildingEquipmentExchangeProcessExecuted::setLockedCards);
    }

    private void updateCharacterEnergyAndAccountAndCityBalances(
            BuildingExchangerExchangeExecutedReqDto reqDto, CityDto cityDto,
            BuildingExchangerExchangeData buildingExchangeData,
            BuildingEquipmentExchangeProcessExecuted buildingEquipmentExchangeProcessExecuted) {
        var auditSource = AuditSource.ofBuilding(BUILDING_TYPE);

        subtractFromCharacterEnergy(
                reqDto, buildingExchangeData.getCharacterEnergy(), auditSource,
                buildingEquipmentExchangeProcessExecuted::setCharacterEnergyOperationRes);

        subtractTaxFromAccountBalance(
                reqDto, buildingExchangeData.getCraftTax(), auditSource,
                buildingEquipmentExchangeProcessExecuted::setAccountBalanceOperationReq);

        addTaxToCityBalance(
                cityDto.getId(), buildingExchangeData.getCraftTax(), auditSource,
                buildingEquipmentExchangeProcessExecuted::setCityBalanceOperationReq);
    }

    private void subtractFromCharacterEnergy(BuildingExchangerExchangeExecutedReqDto reqDto, Double quantity, AuditSource source,
            Consumer<CharacterEnergyOperationResDto> consumer) {
        if (quantity <= 0) {
            return;
        }
        characterCommunicationService.handleCharacterEnergyOperation(
                CharacterEnergyOperationReqDto.of(reqDto.getAccountId(), reqDto.getCharacterId(), SUBTRACT, quantity, source,
                        EXCHANGE_EXECUTE),
                consumer);
    }

    private void subtractTaxFromAccountBalance(BuildingExchangerExchangeExecutedReqDto reqDto, Double quantity, AuditSource source,
            Consumer<AccountBalanceOperationReqDto> consumer) {
        if (quantity <= 0) {
            return;
        }
        accountBalanceCommunicationService.handleOperation(
                AccountBalanceOperationReqDto.of(reqDto.getAccountId(), SUBTRACT, Currency.GOLD, quantity, source,
                        TAX_FOR_EXCHANGE_EXECUTE),
                consumer);
    }

    private void addTaxToCityBalance(Long cityId, Double quantity, AuditSource source, Consumer<CityBalanceOperationReqDto> consumer) {
        if (quantity <= 0) {
            return;
        }
        cityBalanceCommunicationService.handleOperation(
                CityBalanceOperationReqDto.of(cityId, ADD, Currency.GOLD, quantity, source, TAX_FOR_EXCHANGE_EXECUTE),
                consumer);
    }

}
