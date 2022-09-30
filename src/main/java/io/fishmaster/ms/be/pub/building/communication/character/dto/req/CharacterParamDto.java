package io.fishmaster.ms.be.pub.building.communication.character.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CharacterParamDto {
    Boolean energy;
    Boolean skillSlots;
    Boolean traitSlots;
    Boolean specialities;

    public static CharacterParamDto of(boolean energy, boolean skillSlots, boolean traitSlots, boolean specialities) {
        return new CharacterParamDto(energy, skillSlots, traitSlots, specialities);
    }
}
