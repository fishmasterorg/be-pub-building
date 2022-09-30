package io.fishmaster.ms.be.pub.building.communication.card.inventory.uri;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.pub.building.communication.card.inventory.properties.CardInventoryProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CardInventoryUriServiceImpl implements CardInventoryUriService {

    private final CardInventoryProperties cardInventoryProperties;

    @Override
    public URI getCardFetchUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getCardFetch())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getAccountCardCreateUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getAccountCardCreate())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getAccountCardLockUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getAccountCardLock())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getAccountCardLockForCraftUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getAccountCardLockForCraft())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getAccountCardCompleteUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getAccountCardComplete())
                .build()
                .encode()
                .toUri();
    }

    @Override
    public URI getAccountCardRollbackUri() {
        return UriComponentsBuilder.fromUriString(cardInventoryProperties.getUri())
                .path(cardInventoryProperties.getPath().getAccountCardRollback())
                .build()
                .encode()
                .toUri();
    }

}
