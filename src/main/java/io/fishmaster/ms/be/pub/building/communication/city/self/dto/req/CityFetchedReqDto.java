package io.fishmaster.ms.be.pub.building.communication.city.self.dto.req;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
public class CityFetchedReqDto {
    Set<Long> ids;
    String accountId;

    public static CityFetchedReqDto of(Collection<Long> ids) {
        return new CityFetchedReqDto(new HashSet<>(ids), null);
    }

    public static CityFetchedReqDto of(String accountId) {
        return new CityFetchedReqDto(Set.of(), accountId);
    }
}