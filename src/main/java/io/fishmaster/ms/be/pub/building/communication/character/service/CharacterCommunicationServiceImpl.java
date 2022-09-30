package io.fishmaster.ms.be.pub.building.communication.character.service;

import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.experience.CharacterExperienceReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterExistsReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.uri.CharacterUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CharacterCommunicationServiceImpl implements CharacterCommunicationService {

    private final RestTemplate characterRestTemplate;
    private final CharacterUriService characterUriService;

    @Override
    public List<CharacterDto> fetchAllCharacter(CharacterFetchedReqDto reqDto, CharacterParamDto paramDto) {
        var uri = characterUriService.getCharacterFetchUri(paramDto);
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = new ParameterizedTypeReference<List<CharacterDto>>() {
        };

        try {
            log.info("Request to fetch all characters send to character ms. {}, params = {}", reqDto, paramDto);
            var characterDtos = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on fetch all characters from character ms. Size = {}", characterDtos.size());
            return characterDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on fetch all characters request. %s, params = %s. Error = %s"
                            .formatted(reqDto, paramDto, e.getMessage())
            );
        }
    }

    @Override
    public Boolean existsCharacter(CharacterExistsReqDto reqDto) {
        var uri = characterUriService.getCharacterExistsUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = Boolean.class;

        try {
            log.info("Request to exists character send to character ms. {}", reqDto);
            var exists = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on exists character from character ms. {}", exists);
            return exists;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on exists character request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage()));
        }
    }

    @Override
    public CharacterDto lockCharacterStatus(CharacterStatusReqDto characterStatusReqDto, CharacterParamDto paramDto) {
        var uri = characterUriService.getCharacterStatusLockUri(paramDto);
        var httpEntity = new HttpEntity<>(characterStatusReqDto);
        var responseType = CharacterDto.class;

        try {
            log.info("Request to lock character status send to character ms. {}, params = {}", characterStatusReqDto, paramDto);
            var characterDto = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on lock character status from character ms. {}", characterDto);
            return characterDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on lock character status request. %s, params = %s. Error = %s"
                            .formatted(characterStatusReqDto, paramDto, e.getMessage())
            );
        }
    }

    @Override
    public void completeCharacterStatus(CharacterStatusReqDto characterStatusReqDto) {
        var uri = characterUriService.getCharacterStatusCompleteUri();
        var httpEntity = new HttpEntity<>(characterStatusReqDto);
        var responseType = String.class;

        try {
            log.info("Request to complete character status send to character ms. {}", characterStatusReqDto);
            var httpStatus = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getStatusCode();
            log.info("Response on complete character status from character ms. Http status = {}", httpStatus);
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on complete character status request. %s. Error = %s"
                            .formatted(characterStatusReqDto, e.getMessage())
            );
        }
    }

    @Override
    public void rollbackCharacterStatus(CharacterStatusReqDto characterStatusReqDto) {
        var uri = characterUriService.getCharacterStatusRollbackUri();
        var httpEntity = new HttpEntity<>(characterStatusReqDto);
        var responseType = String.class;

        try {
            log.info("Request to rollback character status send to character ms. {}", characterStatusReqDto);
            var httpStatus = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getStatusCode();
            log.info("Response on rollback character status from character ms. Http status = {}", httpStatus);
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on rollback character status request. %s. Error = %s"
                            .formatted(characterStatusReqDto, e.getMessage())
            );
        }
    }

    @Override
    public CharacterEnergyOperationResDto handleCharacterEnergyOperation(CharacterEnergyOperationReqDto reqDto) {
        var uri = characterUriService.getCharacterEnergyHandleOperationUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = CharacterEnergyOperationResDto.class;

        try {
            log.info("Request to handle character energy operation send to character ms. {}", reqDto);
            var resDto = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on handle character energy operation from character ms. {}", resDto);
            return resDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on handle character energy operation request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

    @Override
    public CharacterDto addExperiences(CharacterExperienceReqDto reqDto) {
        var uri = characterUriService.getCharacterExperienceAddUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = CharacterDto.class;

        try {
            log.info("Request to add experience send to character ms. {}", reqDto);
            var characterDto = characterRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on add experience from character ms. {}", characterDto);
            return characterDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from character ms on add experience request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

}
