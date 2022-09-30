package io.fishmaster.ms.be.pub.building.service.building.pub.task;

import static io.fishmaster.ms.be.commons.constant.Status.CANCELED;
import static io.fishmaster.ms.be.commons.constant.Status.COLLECTED;
import static io.fishmaster.ms.be.commons.constant.Status.DONE;
import static io.fishmaster.ms.be.commons.constant.Status.IN_PROCESS;
import static io.fishmaster.ms.be.commons.utility.DateTimeUtility.atEndOfDay;
import static io.fishmaster.ms.be.commons.utility.DateTimeUtility.atStartOfDay;

import java.time.Clock;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.LoadingCache;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.utility.DateTimeUtility;
import io.fishmaster.ms.be.commons.utility.LockUtility;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.task.PubBuildingDailyTaskConverter;
import io.fishmaster.ms.be.pub.building.converter.task.PubBuildingDailyTaskHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.PubBuildingDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.prepared.PubBuildingPreparedDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.document.task.PubBuildingDailyTaskHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.task.PubBuildingDailyTaskHistoryRepository;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.model.BuildingDailyTaskProcessCollected;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCollectedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskExistsReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskStartedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingDailyTaskServiceImpl implements PubBuildingDailyTaskService {

    private final CityCommunicationService cityCommunicationService;
    private final CharacterCommunicationService characterCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;

    private final PubBuildingPreparedDailyTaskRepository pubBuildingPreparedDailyTaskRepository;

    private final PubBuildingDailyTaskRepository pubBuildingDailyTaskRepository;
    private final PubBuildingDailyTaskHistoryRepository pubBuildingDailyTaskHistoryRepository;

    private final LoadingCache<String, ReentrantLock> pubBuildingDailyTaskStartedLockRegistry;

    private final Clock utcClock;

    @Transactional
    @Override
    public List<BuildingDailyTaskUiDto> fetch(BuildingDailyTaskFetchedReqDto reqDto) {
        var currentWeekDay = DateTimeUtility.getCurrentWeekDay(utcClock);
        var startOfDay = atStartOfDay(utcClock);
        var endOfDay = atEndOfDay(utcClock);

        var pubBuildingDailyTasks =
                pubBuildingDailyTaskRepository.findAllByAccountIdAndPubBuilding_Id(reqDto.getAccountId(), reqDto.getBuildingId());
        var pubBuildingDailyTaskHistories =
                pubBuildingDailyTaskHistoryRepository.findAllByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
                        reqDto.getAccountId(), reqDto.getBuildingId(), COLLECTED, startOfDay, endOfDay);

        var configurationIdsIsAlreadyStartedInCurrentDay = pubBuildingDailyTasks.stream()
                .filter(task -> startOfDay < task.getCreatedDate() && task.getCreatedDate() < endOfDay)
                .map(PubBuildingDailyTask::getConfigurationId)
                .collect(Collectors.toSet());
        var configurationIdsIsAlreadyCollectedInCurrentDay = pubBuildingDailyTaskHistories.stream()
                .map(PubBuildingDailyTaskHistory::getConfigurationId)
                .collect(Collectors.toSet());

        var pubBuildingPreparedDailyTasks =
                pubBuildingPreparedDailyTaskRepository.findAllByPubBuilding_IdAndWeekDay(reqDto.getBuildingId(), currentWeekDay);
        var dailyTaskConfigurationDtoMap = configurationsStorageService.getDailyTaskConfigurations().getMapWithKeyId();

        var pubBuildingPreparedDailyTasksStream = pubBuildingPreparedDailyTasks.stream()
                .filter(task -> !configurationIdsIsAlreadyStartedInCurrentDay.contains(task.getConfigurationId()))
                .filter(task -> !configurationIdsIsAlreadyCollectedInCurrentDay.contains(task.getConfigurationId()))
                .map(task -> {
                    var configurationDto = dailyTaskConfigurationDtoMap.get(task.getConfigurationId());
                    return PubBuildingDailyTaskConverter.toUiDto(task, configurationDto);
                });

        var pubBuildingDailyTasksStream = pubBuildingDailyTasks.stream()
                .map(task -> {
                    var configurationDto = dailyTaskConfigurationDtoMap.get(task.getConfigurationId());
                    return PubBuildingDailyTaskConverter.toUiDto(task, configurationDto);
                });

        return Stream.concat(pubBuildingDailyTasksStream, pubBuildingPreparedDailyTasksStream)
                .toList();
    }

    @Override
    public Boolean exists(BuildingDailyTaskExistsReqDto reqDto) {
        return pubBuildingDailyTaskRepository.existsByCharacterIdsIn(Set.of(reqDto.getCharacterId()));
    }

    @Transactional
    @Override
    public BuildingDailyTaskUiDto startTask(BuildingDailyTaskStartedReqDto reqDto) {
        return LockUtility.lock(
                () -> {
                    var lockRegistryKey = String.join(
                            "_", reqDto.getAccountId(), String.valueOf(reqDto.getBuildingId()),
                            reqDto.getConfigurationId());
                    return pubBuildingDailyTaskStartedLockRegistry.getUnchecked(lockRegistryKey);
                },
                () -> {
                    var dailyTaskConfigurationDto = configurationsStorageService.getDailyTaskConfigurations()
                            .getById(reqDto.getConfigurationId());

                    validateStartTask(reqDto, dailyTaskConfigurationDto);

                    var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

                    var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

                    var selectedCharacters = reqDto.getSelectedCharacters();

                    var characterDtos = characterCommunicationService.fetchAllCharacter(
                            CharacterFetchedReqDto.of(selectedCharacters.values(), reqDto.getAccountId(), cityDto.getId()),
                            CharacterParamDto.of(false, false, false, true));

                    if (characterDtos.size() < selectedCharacters.size()) {
                        throw new ServiceException(
                                ExceptionCode.CHARACTERS_ARE_NOT_IN_CITY,
                                "Not all characters = %s are in the city with id = %s"
                                        .formatted(Arrays.toString(selectedCharacters.values().toArray()), cityDto.getId()));
                    }

                    checkStartTaskRequirements(selectedCharacters, dailyTaskConfigurationDto, characterDtos);

                    var pubBuildingDailyTask = pubBuildingDailyTaskRepository.save(
                            PubBuildingDailyTaskConverter.toEntityWithStatusInProcess(reqDto, pubBuilding, dailyTaskConfigurationDto));

                    return PubBuildingDailyTaskConverter.toUiDto(pubBuildingDailyTask, dailyTaskConfigurationDto);
                });
    }

    @Transactional
    @Override
    public BuildingDailyTaskUiDto cancelTask(BuildingDailyTaskCanceledReqDto reqDto) {
        var pubBuildingDailyTask = pubBuildingDailyTaskRepository.findWithLockByIdAndAccountIdAndStatusIn(
                reqDto.getBuildingDailyTaskId(), reqDto.getAccountId(), Set.of(IN_PROCESS, DONE))
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Pub building daily task with id = %s and account id = %s and statuses = %s not exists"
                                .formatted(reqDto.getBuildingDailyTaskId(), reqDto.getAccountId(),
                                        Arrays.toString(Set.of(IN_PROCESS, DONE).toArray()))));

        var dailyTaskConfigurationDto = configurationsStorageService.getDailyTaskConfigurations()
                .getById(pubBuildingDailyTask.getConfigurationId());

        pubBuildingDailyTask.setStatus(CANCELED);

        pubBuildingDailyTaskHistoryRepository.save(
                PubBuildingDailyTaskHistoryConverter.toEntity(pubBuildingDailyTask, utcClock));

        pubBuildingDailyTaskRepository.delete(pubBuildingDailyTask);

        return PubBuildingDailyTaskConverter.toUiDto(pubBuildingDailyTask, dailyTaskConfigurationDto);
    }

    @Transactional
    @Override
    public BuildingDailyTaskUiDto collectTask(BuildingDailyTaskCollectedReqDto reqDto) {
        var buildingDailyTaskProcessCollected = new BuildingDailyTaskProcessCollected(reqDto.getAccountId());
        try {
            var pubBuildingDailyTask = pubBuildingDailyTaskRepository.findWithLockByIdAndAccountIdAndStatusIn(
                    reqDto.getBuildingDailyTaskId(), reqDto.getAccountId(), Set.of(DONE))
                    .orElseThrow(() -> new ServiceException(
                            ExceptionCode.INNER_SERVICE,
                            "Account pub building daily task with id = %s and account id = %s and statuses = %s not exists"
                                    .formatted(reqDto.getBuildingDailyTaskId(), reqDto.getAccountId(),
                                            Arrays.toString(Set.of(DONE).toArray()))));

            var cityDto = cityCommunicationService.fetchCity(pubBuildingDailyTask.getPubBuilding().getCityId());

            pubBuildingDailyTask.setStatus(COLLECTED);

            var dailyTaskConfigurationDto = configurationsStorageService.getDailyTaskConfigurations()
                    .getById(pubBuildingDailyTask.getConfigurationId());

            var card = dailyTaskConfigurationDto.getResult().getCard();
            var accountCardDtos = IntStream.range(0, card.getQuantity())
                    .mapToObj(value -> AccountCardDto.of(reqDto.getAccountId(), card.getId(), CardSource.PUB_DAILY_TASK))
                    .toList();

            cardInventoryCommunicationService.createAccountCards(accountCardDtos, buildingDailyTaskProcessCollected::setCards);

            var pubBuildingDailyTaskHistory = pubBuildingDailyTaskHistoryRepository.save(
                    PubBuildingDailyTaskHistoryConverter.toEntity(
                            pubBuildingDailyTask, buildingDailyTaskProcessCollected.getCards(), utcClock));

            pubBuildingDailyTaskRepository.delete(pubBuildingDailyTask);

            buildingDailyTaskProcessCollected.updateCards(Status.ACTIVE, cityDto.getId());
            buildingDailyTaskProcessCollected.complete(cardInventoryCommunicationService::completeAccountCards);

            return PubBuildingDailyTaskHistoryConverter.toUiDto(pubBuildingDailyTaskHistory, dailyTaskConfigurationDto);
        } catch (Exception e) {
            log.error("Error while on collect daily task in pub building. {}. Error = {}", reqDto, e.getMessage());

            buildingDailyTaskProcessCollected.rollback(cardInventoryCommunicationService::rollbackAccountCards);

            throw e;
        }
    }

    private void validateStartTask(BuildingDailyTaskStartedReqDto reqDto, DailyTaskConfigurationDto dailyTaskConfigurationDto) {
        var currentWeekDay = DateTimeUtility.getCurrentWeekDay(utcClock);

        var exists = pubBuildingPreparedDailyTaskRepository.existsByPubBuilding_IdAndWeekDayAndConfigurationId(
                reqDto.getBuildingId(), currentWeekDay, reqDto.getConfigurationId());
        if (!exists) {
            throw new ServiceException(
                    ExceptionCode.DAILY_TASK_ARE_NOT_PREPARED_FOR_THE_CURRENT_DAY,
                    "Daily task configuration with id = %s not prepared for current day = %s or for building with id = %s"
                            .formatted(reqDto.getConfigurationId(), currentWeekDay, reqDto.getBuildingId()));
        }

        exists = pubBuildingDailyTaskHistoryRepository.existsByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(
                reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId(),
                COLLECTED, atStartOfDay(utcClock), atEndOfDay(utcClock));
        if (exists) {
            throw new ServiceException(
                    ExceptionCode.DAILY_TASK_ALREADY_COLLECTED,
                    "Pub building daily task with account id = %s and building id = %s and configuration id = %s already collected in current day = %s"
                            .formatted(reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId(), currentWeekDay));
        }

        exists = pubBuildingDailyTaskRepository.existsByAccountIdAndPubBuilding_IdAndConfigurationId(
                reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId());
        if (exists) {
            throw new ServiceException(
                    ExceptionCode.DAILY_TASK_ALREADY_STARTED,
                    "Pub building daily task with account id = %s and building id = %s and configuration id = %s already started"
                            .formatted(reqDto.getAccountId(), reqDto.getBuildingId(), reqDto.getConfigurationId()));
        }

        var characterIds = new HashSet<>(reqDto.getSelectedCharacters().values());

        exists = pubBuildingDailyTaskRepository.existsByCharacterIdsIn(characterIds);
        if (exists) {
            throw new ServiceException(
                    ExceptionCode.CHARACTERS_ALREADY_ASSIGNED,
                    "One of the characters = %s is already assigned to each daily task"
                            .formatted(Arrays.toString(characterIds.toArray(Long[]::new))));
        }

        var required = dailyTaskConfigurationDto.getRequired();
        if (reqDto.getSelectedCharacters().size() < required.getSpecialities().size()) {
            throw new ServiceException(
                    ExceptionCode.FEWER_CHARACTERS_THAN_REQUIRED,
                    "There are fewer characters = %s than required = %s"
                            .formatted(reqDto.getSelectedCharacters().size(), required.getSpecialities().size()));
        }

        if (characterIds.size() < reqDto.getSelectedCharacters().size()) {
            throw new ServiceException(
                    ExceptionCode.FEWER_CHARACTERS_THAN_SELECTED,
                    "There are fewer characters = %s than selected = %s"
                            .formatted(characterIds.size(), reqDto.getSelectedCharacters().size()));
        }
    }

    private void checkStartTaskRequirements(Map<Integer, Long> selectedCharacters, DailyTaskConfigurationDto dailyTaskConfigurationDto,
            List<CharacterDto> characterDtos) {
        var requiredSpecialityMap = dailyTaskConfigurationDto.getRequired().getSpecialities().stream()
                .collect(Collectors.toMap(DailyTaskConfigurationDto.Speciality::getSequenceNumber, Function.identity()));
        var characterMap = characterDtos.stream()
                .collect(Collectors.toMap(CharacterDto::getId, Function.identity()));

        var allMatch = selectedCharacters.entrySet().stream()
                .allMatch(entry -> {
                    var speciality = requiredSpecialityMap.get(entry.getKey());
                    var characterDto = characterMap.get(entry.getValue());

                    var characterSpecialityMap = characterDto.getSpecialities().stream()
                            .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), Function.identity()));

                    var characterSpecialityDto = characterSpecialityMap.get(speciality.getName());

                    return characterSpecialityDto.getLevel() >= speciality.getLevel();
                });

        if (!allMatch) {
            var specialities = dailyTaskConfigurationDto.getRequired().getSpecialities().stream()
                    .map(DailyTaskConfigurationDto.Speciality::getName)
                    .toArray(Speciality[]::new);

            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CHARACTER_SPECIALITY_LEVEL,
                    "One of the characters = %s have not enough speciality = %s level"
                            .formatted(Arrays.toString(selectedCharacters.values().toArray()), Arrays.toString(specialities)));
        }
    }

}
