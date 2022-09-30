package io.fishmaster.ms.be.pub.building.db.jpa.field;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import io.fishmaster.ms.be.commons.model.result.ResultExperience;

@Converter
public class ListResultExperienceAttributeConverter implements AttributeConverter<List<ResultExperience>, String> {

    private static final TypeReference<List<ResultExperience>> TYPE_REF = new TypeReference<>() {};
    private final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Override
    public String convertToDatabaseColumn(List<ResultExperience> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<ResultExperience> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, TYPE_REF);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}
