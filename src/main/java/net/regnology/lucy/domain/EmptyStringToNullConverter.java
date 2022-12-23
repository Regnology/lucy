package net.regnology.lucy.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

@Converter
public class EmptyStringToNullConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return StringUtils.defaultIfBlank(attribute, "");
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
