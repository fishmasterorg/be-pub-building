package io.fishmaster.ms.be.pub.building.db.jpa.field;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
public class SetLongAttributeConverter implements AttributeConverter<Set<Long>, String> {

    public static final String DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(Set<Long> attribute) {
        if (attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(value -> Long.toString(value))
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<Long> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return new HashSet<>();
        }
        return Stream.of(dbData.split(DELIMITER))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(HashSet::new));
    }

}
