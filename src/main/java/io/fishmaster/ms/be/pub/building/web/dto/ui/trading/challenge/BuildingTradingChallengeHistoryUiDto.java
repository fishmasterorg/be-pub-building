package io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.character.Gender;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingTradingChallengeHistoryUiDto {
    Long id;
    String accountNickname;
    CharacterSkin characterSkin;
    Data data;
    ResultUiDto result;
    Status status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CharacterSkin {
        Gender gender;
        Integer hair;
        Integer face;
        Integer body;
        Integer cloth;
        Integer attribute;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        Long cardId;
        Integer cardsQuantity;
        Double cost;
    }
}
