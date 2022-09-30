package io.fishmaster.ms.be.pub.building.communication.account.self.uri;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.account.self.properties.AccountProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountUriServiceImpl implements AccountUriService {

    private final AccountProperties accountProperties;

    @Override
    public URI getByParamUri(String key, String value) {
        return UriComponentsBuilder.fromUriString(accountProperties.getUri())
                .path(accountProperties.getPath().getGetByParam())
                .queryParam(key, value)
                .build()
                .encode()
                .toUri();
    }

}
