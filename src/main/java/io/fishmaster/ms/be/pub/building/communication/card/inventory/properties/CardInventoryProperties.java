package io.fishmaster.ms.be.pub.building.communication.card.inventory.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms.card-inventory")
public class CardInventoryProperties {

    private String uri;
    private Path path;
    private Timeout timeout;

    @Getter
    @Setter
    public static class Path {
        private String cardFetch;

        private String accountCardCreate;
        private String accountCardLock;
        private String accountCardLockForCraft;
        private String accountCardComplete;
        private String accountCardRollback;
    }

    @Getter
    @Setter
    public static class Timeout {
        private Integer connect;
        private Integer read;
    }

}
