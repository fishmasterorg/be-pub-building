package io.fishmaster.ms.be.pub.building.communication.card.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
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
public class CardDto {
    Long id;
    CardType type;
    Meta meta;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        Integer level;
        Double value;
        BuildingType building;
    }

    public static CardDto of(Long id) {
        return new CardDto(id, null, new Meta());
    }
}
