package io.fishmaster.ms.be.pub.building.configuration.rest;

import java.time.Duration;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.pub.building.communication.account.balance.properties.AccountBalanceProperties;
import io.fishmaster.ms.be.pub.building.communication.account.self.properties.AccountProperties;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.properties.CardInventoryProperties;
import io.fishmaster.ms.be.pub.building.communication.character.properties.CharacterProperties;
import io.fishmaster.ms.be.pub.building.communication.city.balance.properties.CityBalanceProperties;
import io.fishmaster.ms.be.pub.building.communication.city.self.properties.CityProperties;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.properties.ConfigurationsStorageProperties;
import io.fishmaster.ms.be.pub.building.configuration.rest.interceptor.JsonContentTypeRestTemplateInterceptor;
import io.fishmaster.ms.be.pub.building.configuration.rest.interceptor.MDCRestTemplateInterceptor;
import io.fishmaster.ms.be.pub.building.configuration.rest.properties.RestTemplateProperties;
import io.fishmaster.ms.be.pub.building.exception.communication.RestTemplateErrorHandler;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(RestTemplateProperties restTemplateProperties) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(restTemplateProperties.getMaxTotal());
        manager.setDefaultMaxPerRoute(restTemplateProperties.getDefaultMaxPerRoute());
        return manager;
    }

    @Bean
    public RequestConfig requestConfig(RestTemplateProperties restTemplateProperties) {
        return RequestConfig.custom()
                .setSocketTimeout(restTemplateProperties.getSocketTimeout())
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) {
        return HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(HttpClient httpClient) {
        return new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean(name = "configurationsStorageRestTemplate")
    public RestTemplate configurationsStorageRestTemplate(RestTemplateBuilder restTemplateBuilder, ConfigurationsStorageProperties configurationsStorageProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_CONFIGURATIONS_STORAGE))
                .rootUri(configurationsStorageProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(configurationsStorageProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(configurationsStorageProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "accountRestTemplate")
    public RestTemplate accountRestTemplate(RestTemplateBuilder restTemplateBuilder, AccountProperties accountProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_ACCOUNT))
                .rootUri(accountProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(accountProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(accountProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "accountBalanceRestTemplate")
    public RestTemplate accountBalanceRestTemplate(RestTemplateBuilder restTemplateBuilder, AccountBalanceProperties accountBalanceProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_ACCOUNT_BALANCE))
                .rootUri(accountBalanceProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(accountBalanceProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(accountBalanceProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "cityRestTemplate")
    public RestTemplate cityRestTemplate(RestTemplateBuilder restTemplateBuilder, CityProperties cityProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_CITY))
                .rootUri(cityProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(cityProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(cityProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "cityBalanceRestTemplate")
    public RestTemplate cityBalanceRestTemplate(RestTemplateBuilder restTemplateBuilder, CityBalanceProperties cityBalanceProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_CITY_BALANCE))
                .rootUri(cityBalanceProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(cityBalanceProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(cityBalanceProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "characterRestTemplate")
    public RestTemplate characterRestTemplate(RestTemplateBuilder restTemplateBuilder, CharacterProperties characterProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_CHARACTER))
                .rootUri(characterProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(characterProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(characterProperties.getTimeout().getRead()))
                .build();
    }

    @Bean(name = "cardInventoryRestTemplate")
    public RestTemplate cardInventoryRestTemplate(RestTemplateBuilder restTemplateBuilder, CardInventoryProperties cardInventoryProperties) {
        return restTemplateBuilder.errorHandler(new RestTemplateErrorHandler(ServiceName.BE_CARD_INVENTORY))
                .rootUri(cardInventoryProperties.getUri())
                .interceptors(new JsonContentTypeRestTemplateInterceptor())
                .additionalInterceptors(new MDCRestTemplateInterceptor())
                .setConnectTimeout(Duration.ofMillis(cardInventoryProperties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(cardInventoryProperties.getTimeout().getRead()))
                .build();
    }

}
