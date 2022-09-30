package io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
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
public class BuildingTradingOrderUiDto {
    Long id;
    Integer sequenceNumber;
    Long buildingId;
    Speciality speciality;
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
    public static class Speciality {
        io.fishmaster.ms.be.commons.constant.character.Speciality name;
        Integer level;
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
        Double energyForDeliver;
        Double energyForCancel;
        Double cost;
    }
}
