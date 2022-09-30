package io.fishmaster.ms.be.pub.building.db.jpa.field;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.model.TradingChallengeData;

@Converter
public class TradingChallengeDataAttributeConverter implements AttributeConverter<TradingChallengeData, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TradingChallengeData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Override
    public TradingChallengeData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, TradingChallengeData.class);
        } catch (JsonProcessingException e) {
            return new TradingChallengeData();
        }
    }
}
