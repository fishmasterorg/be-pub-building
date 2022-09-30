package io.fishmaster.ms.be.pub.building.communication.character.dto.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.fishmaster.ms.be.commons.constant.Rarity;
import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.character.SkillEffect;
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
public class CharacterSkillSlotDto {
    Long id;
    Long characterId;
    Configuration skillConfiguration;
    Status status;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Configuration {
        String id;
        Set<BuildingType> buildings;
        Rarity rarity;
        Effect effect;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Effect {
        Double chance;
        SkillEffect name;
        Double quantity;
        Set<Integer> levels;
    }
}
