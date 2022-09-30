package io.fishmaster.ms.be.pub.building.communication.character.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.experience.CharacterExperienceReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterExistsReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;

public interface CharacterCommunicationService {

    List<CharacterDto> fetchAllCharacter(CharacterFetchedReqDto reqDto, CharacterParamDto paramDto);

    default List<CharacterDto> fetchCharacter(Set<Long> ids, String accountId, Long cityId, CharacterParamDto paramDto) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return fetchAllCharacter(CharacterFetchedReqDto.of(ids, accountId, cityId), paramDto);
    }

    default CharacterDto fetchCharacter(Long id, String accountId, Long cityId, CharacterParamDto paramDto) {
        var characterState = fetchCharacter(Set.of(id), accountId, cityId, paramDto);
        if (characterState.isEmpty()) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Character with id = %s and account id = %s and city id = %s and params = %s not exists"
                            .formatted(id, accountId, characterState, paramDto));
        }
        return characterState.get(0);
    }

    Boolean existsCharacter(CharacterExistsReqDto reqDto);

    default Boolean existsCharacter(Long id, String accountId, Long cityId) {
        return existsCharacter(CharacterExistsReqDto.of(id, accountId, cityId));
    }

    CharacterDto lockCharacterStatus(CharacterStatusReqDto reqDto, CharacterParamDto paramDto);

    default CharacterDto lockCharacterStatus(
            Long id, String accountId, Long cityId, Status status,
            CharacterParamDto paramDto, Consumer<CharacterStatusReqDto> consumer) {
        var reqDto = CharacterStatusReqDto.of(id, accountId, cityId, status);
        var characterDto = lockCharacterStatus(reqDto, paramDto);
        consumer.accept(reqDto);
        return characterDto;
    }

    default CharacterDto lockCharacterStatus(CharacterStatusReqDto reqDto, CharacterParamDto paramDto,
            Consumer<CharacterStatusReqDto> consumer) {
        var characterDto = lockCharacterStatus(reqDto, paramDto);
        consumer.accept(reqDto);
        return characterDto;
    }

    void completeCharacterStatus(CharacterStatusReqDto reqDto);

    void rollbackCharacterStatus(CharacterStatusReqDto reqDto);

    CharacterEnergyOperationResDto handleCharacterEnergyOperation(CharacterEnergyOperationReqDto reqDto);

    default void handleCharacterEnergyOperation(CharacterEnergyOperationReqDto reqDto,
            Consumer<CharacterEnergyOperationResDto> consumer) {
        consumer.accept(handleCharacterEnergyOperation(reqDto));
    }

    CharacterDto addExperiences(CharacterExperienceReqDto reqDto);

    default CharacterDto addExperiences(
            String accountId, Long characterId, List<ResultExperience> experiences,
            AuditSource auditSource, AuditAction auditAction) {
        var characterExperienceReqDto = experiences.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(ResultExperience::getSpeciality, ResultExperience::getQuantity),
                        exps -> new CharacterExperienceReqDto(accountId, characterId, exps, auditSource, auditAction)));

        return addExperiences(characterExperienceReqDto);
    }

}
