package io.fishmaster.ms.be.pub.building.configuration.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.fishmaster.ms.be.pub.building.configuration.cache.properties.CacheProperties;

@EnableCaching
@Configuration
public class CacheConfiguration {

    @Bean
    public Caffeine<Object, Object> caffeineConfiguration(CacheProperties cacheProperties) {
        return Caffeine.newBuilder()
                .expireAfterWrite(cacheProperties.getExpireAfterWriteInMs(), TimeUnit.MILLISECONDS);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeineConfiguration) {
        var caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeineConfiguration);
        return caffeineCacheManager;
    }

    @Bean
    public LoadingCache<String, ReentrantLock> pubBuildingDailyTaskStartedLockRegistry(CacheProperties cacheProperties) {
        return buildLoadingCache(cacheProperties.getDailyTask().getExpireAfterWriteInMs());
    }

    @Bean
    public LoadingCache<String, ReentrantLock> pubBuildingExchangerExchangeExecutedLockRegistry(CacheProperties cacheProperties) {
        return buildLoadingCache(cacheProperties.getExchangerExchange().getExpireAfterWriteInMs());
    }

    @Bean
    public LoadingCache<String, ReentrantLock> pubBuildingBarmanOfferExecutedLockRegistry(CacheProperties cacheProperties) {
        return buildLoadingCache(cacheProperties.getBarmanOffer().getExpireAfterWriteInMs());
    }

    @Bean
    public LoadingCache<String, ReentrantLock> pubBuildingExchangerOfferExecutedLockRegistry(CacheProperties cacheProperties) {
        return buildLoadingCache(cacheProperties.getExchangerOffer().getExpireAfterWriteInMs());
    }

    private LoadingCache<String, ReentrantLock> buildLoadingCache(Long expireAfterWriteInMs) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(expireAfterWriteInMs, TimeUnit.MILLISECONDS)
                .softValues()
                .build(new CacheLoader<>() {
                    @Override
                    public ReentrantLock load(String key) {
                        return new ReentrantLock(true);
                    }
                });
    }

}
