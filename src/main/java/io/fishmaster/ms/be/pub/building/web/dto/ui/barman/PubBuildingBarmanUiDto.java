package io.fishmaster.ms.be.pub.building.web.dto.ui.barman;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.offer.BuildingBarmanOfferUiDto;
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
public class PubBuildingBarmanUiDto {
    Set<Long> lastCharacterIdsUsed;
    List<BuildingBarmanOfferUiDto> offers;
}
