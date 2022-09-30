package io.fishmaster.ms.be.pub.building.communication.city.self.uri;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.city.self.properties.CityProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CityUriServiceImpl implements CityUriService {

    private final CityProperties cityProperties;

    @Override
    public URI getCityFetchUri() {
        return UriComponentsBuilder.fromUriString(cityProperties.getUri())
                .path(cityProperties.getPath().getCityFetch())
                .build()
                .encode()
                .toUri();
    }

}
