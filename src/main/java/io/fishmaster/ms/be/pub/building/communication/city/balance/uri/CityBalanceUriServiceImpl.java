package io.fishmaster.ms.be.pub.building.communication.city.balance.uri;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.city.balance.properties.CityBalanceProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CityBalanceUriServiceImpl implements CityBalanceUriService {

    private final CityBalanceProperties cityBalanceProperties;

    @Override
    public URI getHandleOperationUri() {
        return UriComponentsBuilder.fromUriString(cityBalanceProperties.getUri())
                .path(cityBalanceProperties.getPath().getHandleOperation())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getHandleOperationBatchUri() {
        return UriComponentsBuilder.fromUriString(cityBalanceProperties.getUri())
                .path(cityBalanceProperties.getPath().getHandleOperationBatch())
                .build()
                .encode()
                .toUri();
    }
}
