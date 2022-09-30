package io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingExchangerOfferGroupUiDto implements Comparable<BuildingExchangerOfferGroupUiDto> {
    String displayName;
    String description;
    Date date;
    List<BuildingExchangerOfferUiDto> offers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Date {
        Long start;
        Long end;
    }

    @Override
    public int compareTo(BuildingExchangerOfferGroupUiDto o) {
        return Comparator.<BuildingExchangerOfferGroupUiDto, Long>comparing(dto -> dto.getDate().getStart())
                .thenComparing(BuildingExchangerOfferGroupUiDto::getDisplayName)
                .compare(this, o);
    }
}
