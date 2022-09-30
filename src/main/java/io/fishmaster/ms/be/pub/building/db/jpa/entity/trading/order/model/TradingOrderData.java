package io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TradingOrderData {
    @JsonProperty("configuration_id")
    String configurationId;
    @JsonProperty("cards_quantity")
    Integer cardsQuantity;
    @JsonProperty("base_energy")
    Double baseEnergy;
}
