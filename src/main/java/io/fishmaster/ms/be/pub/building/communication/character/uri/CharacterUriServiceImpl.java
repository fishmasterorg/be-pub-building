package io.fishmaster.ms.be.pub.building.communication.character.uri;

import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.properties.CharacterProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CharacterUriServiceImpl implements CharacterUriService {

    private static final String ENERGY_PARAM = "energy";
    private static final String SKILL_SLOTS_PARAM = "skillSlots";
    private static final String TRAIT_SLOTS_PARAM = "traitSlots";
    private static final String SPECIALITIES_PARAM = "specialities";

    private final CharacterProperties characterProperties;

    @Override
    public URI getCharacterFetchUri(CharacterParamDto paramDto) {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterFetch())
                .queryParams(getQueryParams(paramDto))
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterExistsUri() {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterExists())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterStatusLockUri(CharacterParamDto paramDto) {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterStatusLock())
                .queryParams(getQueryParams(paramDto))
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterStatusCompleteUri() {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterStatusComplete())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterStatusRollbackUri() {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterStatusRollback())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterEnergyHandleOperationUri() {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterEnergyHandleOperation())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getCharacterExperienceAddUri() {
        return UriComponentsBuilder.fromUriString(characterProperties.getUri())
                .path(characterProperties.getPath().getCharacterExperienceAdd())
                .build()
                .encode()
                .toUri();
    }

    private LinkedMultiValueMap<String, String> getQueryParams(CharacterParamDto paramDto) {
        var queryParams = new LinkedMultiValueMap<String, String>();

        Optional.ofNullable(paramDto.getEnergy()).ifPresent(addQueryParam(queryParams, ENERGY_PARAM));
        Optional.ofNullable(paramDto.getSkillSlots()).ifPresent(addQueryParam(queryParams, SKILL_SLOTS_PARAM));
        Optional.ofNullable(paramDto.getTraitSlots()).ifPresent(addQueryParam(queryParams, TRAIT_SLOTS_PARAM));
        Optional.ofNullable(paramDto.getSpecialities()).ifPresent(addQueryParam(queryParams, SPECIALITIES_PARAM));

        return queryParams;
    }

    private <T> Consumer<T> addQueryParam(MultiValueMap<String, String> queryParams, String paramName) {
        return value -> queryParams.add(paramName, String.valueOf(value));
    }

}
