package io.fishmaster.ms.be.pub.building.configuration.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Config config;
    private Authentication authentication;
    private Topic topic;

    @Getter
    @Setter
    public static class Config {
        private String groupId;
        private String clientId;
        private Boolean enableAutoCommit;
    }

    @Getter
    @Setter
    public static class Authentication {
        private boolean enable;
        private String securityProtocolConfig;
        private String saslMechanism;
        private String saslJaasConfig;
    }

    @Getter
    @Setter
    public static class Topic {
        private String pubBuildingDailyTaskProgressRefresh;
        private String pubBuildingDailyTaskOutcome;

        private String pubBuildingTradingChallengeRefresh;
        }
}
