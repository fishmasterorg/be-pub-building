package io.fishmaster.ms.be.pub.building.configuration.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private Long expireAfterWriteInMs;
    private DailyTask dailyTask;
    private ExchangerExchange exchangerExchange;
    private ExchangerOffer exchangerOffer;
    private BarmanOffer barmanOffer;

    @Getter
    @Setter
    public static class DailyTask {
        private Long expireAfterWriteInMs;
    }

    @Getter
    @Setter
    public static class ExchangerExchange {
        private Long expireAfterWriteInMs;
    }

    @Getter
    @Setter
    public static class ExchangerOffer {
        private Long expireAfterWriteInMs;
    }

    @Getter
    @Setter
    public static class BarmanOffer {
        private Long expireAfterWriteInMs;
    }
}
