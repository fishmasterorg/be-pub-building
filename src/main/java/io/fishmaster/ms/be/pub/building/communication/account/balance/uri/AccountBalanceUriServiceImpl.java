package io.fishmaster.ms.be.pub.building.communication.account.balance.uri;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.account.balance.properties.AccountBalanceProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AccountBalanceUriServiceImpl implements AccountBalanceUriService {

    private final AccountBalanceProperties accountBalanceProperties;

    @Override
    public URI getHandleOperationUri() {
        return UriComponentsBuilder.fromUriString(accountBalanceProperties.getUri())
                .path(accountBalanceProperties.getPath().getHandleOperation())
                .build()
                .encode()
                .toUri();
    }

}
