package io.fishmaster.ms.be.pub.building.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fishmaster.ms.be.pub.building.configuration.web.filter.MDCFilter;

@Configuration
public class FilterConfiguration {

    @Bean
    public MDCFilter mdcFilter() {
        return new MDCFilter();
    }

}
