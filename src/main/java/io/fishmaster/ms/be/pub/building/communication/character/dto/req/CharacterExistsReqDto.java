package io.fishmaster.ms.be.pub.building.communication.character.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterExistsReqDto {
    Long id;
    String accountId;
    Long cityId;

    public static CharacterExistsReqDto of(Long id, String accountId, Long cityId) {
        return new CharacterExistsReqDto(id, accountId, cityId);
    }
}
