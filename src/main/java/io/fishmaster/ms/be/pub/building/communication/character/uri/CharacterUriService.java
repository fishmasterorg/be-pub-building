package io.fishmaster.ms.be.pub.building.communication.character.uri;


import java.net.URI;

import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;

public interface CharacterUriService {

    URI getCharacterFetchUri(CharacterParamDto paramDto);

    URI getCharacterExistsUri();

    URI getCharacterStatusLockUri(CharacterParamDto paramDto);

    URI getCharacterStatusCompleteUri();

    URI getCharacterStatusRollbackUri();

    URI getCharacterEnergyHandleOperationUri();

    URI getCharacterExperienceAddUri();

}
