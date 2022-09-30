package io.fishmaster.ms.be.pub.building.communication.character.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.skill.CharacterSkillSlotDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.speciality.CharacterSpecialityDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.trait.CharacterTraitSlotDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDto {
    Long id;
    String accountId;
    Integer level;
    Double exp;
    Status status;
    CharacterSkinDto skin;
    CharacterEnergyDto energy;
    @ToString.Exclude
    List<CharacterSkillSlotDto> skillSlots;
    @ToString.Exclude
    List<CharacterTraitSlotDto> traitSlots;
    @ToString.Exclude
    List<CharacterSpecialityDto> specialities;

    public static CharacterDto ofDefault() {
        return CharacterDto.builder()
                .skin(new CharacterSkinDto())
                .energy(new CharacterEnergyDto())
                .skillSlots(List.of())
                .traitSlots(List.of())
                .specialities(List.of())
                .build();
    }
}
