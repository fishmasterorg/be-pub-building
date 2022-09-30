package io.fishmaster.ms.be.pub.building.configuration.rest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "http.connection.pool")
public class RestTemplateProperties {
    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
    private Integer socketTimeout;
}
