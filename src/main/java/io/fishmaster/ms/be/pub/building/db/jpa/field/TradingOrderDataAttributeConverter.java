package io.fishmaster.ms.be.pub.building.db.jpa.field;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.model.TradingOrderData;

@Converter
public class TradingOrderDataAttributeConverter implements AttributeConverter<TradingOrderData, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TradingOrderData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Override
    public TradingOrderData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, TradingOrderData.class);
        } catch (JsonProcessingException e) {
            return new TradingOrderData();
        }
    }
}
