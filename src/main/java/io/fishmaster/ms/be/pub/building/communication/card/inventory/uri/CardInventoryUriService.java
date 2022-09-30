package io.fishmaster.ms.be.pub.building.communication.card.inventory.uri;

import java.net.URI;

public interface CardInventoryUriService {

    URI getCardFetchUri();

    URI getAccountCardCreateUri();

    URI getAccountCardLockUri();

    URI getAccountCardLockForCraftUri();

    URI getAccountCardCompleteUri();

    URI getAccountCardRollbackUri();

}
