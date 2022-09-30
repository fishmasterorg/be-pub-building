package io.fishmaster.ms.be.pub.building.communication.character.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterFetchedReqDto {
    Set<Long> ids;
    String accountId;
    Long cityId;

    public static CharacterFetchedReqDto of(Collection<Long> ids, String accountId, Long cityId) {
        return new CharacterFetchedReqDto(new HashSet<>(ids), accountId, cityId);
    }
}
