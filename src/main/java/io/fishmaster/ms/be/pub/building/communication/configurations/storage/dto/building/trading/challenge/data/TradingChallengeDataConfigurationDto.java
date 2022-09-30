package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradingChallengeDataConfigurationDto {
    String id;
    Integer level;
    Experience experience;
    Long cardId;
    Double cost;

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Experience {
        io.fishmaster.ms.be.commons.constant.character.Speciality speciality;
        Long quantity;
    }
}
