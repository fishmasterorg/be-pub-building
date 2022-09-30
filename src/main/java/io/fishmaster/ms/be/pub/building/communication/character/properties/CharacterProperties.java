package io.fishmaster.ms.be.pub.building.communication.character.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms.character")
public class CharacterProperties {

    private String uri;
    private Path path;
    private Timeout timeout;

    @Getter
    @Setter
    public static class Path {
        private String characterFetch;
        private String characterExists;

        private String characterStatusLock;
        private String characterStatusComplete;
        private String characterStatusRollback;

        private String characterEnergyHandleOperation;

        private String characterExperienceAdd;
    }

    @Getter
    @Setter
    public static class Timeout {
        private Integer connect;
        private Integer read;
    }

}
