package io.fishmaster.ms.be.pub.building.communication.character.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.fishmaster.ms.be.commons.constant.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterStatusReqDto {
    Long id;
    String accountId;
    Long cityId;
    Status status;

    public static CharacterStatusReqDto of(Long id, String accountId, Long cityId, Status status) {
        return new CharacterStatusReqDto(id, accountId, cityId, status);
    }
}
