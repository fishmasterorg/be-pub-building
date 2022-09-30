package io.fishmaster.ms.be.pub.building.communication.character.dto.trait;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.fishmaster.ms.be.commons.constant.Impact;
import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.character.TraitEffect;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterTraitSlotDto {
    Long id;
    Long characterId;
    Configuration traitConfiguration;
    Status status;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Configuration {
        String id;
        Impact impact;
        Set<BuildingType> buildings;
        Effect effect;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Effect {
        Double chance;
        TraitEffect name;
        Double quantity;
    }
}
