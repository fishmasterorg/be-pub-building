package io.fishmaster.ms.be.pub.building.configuration.kafka;

import java.util.HashMap;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import io.fishmaster.ms.be.pub.building.configuration.kafka.interceptor.KafkaByteArrayProducerInterceptor;
import io.fishmaster.ms.be.pub.building.configuration.kafka.interceptor.KafkaProducerInterceptor;
import io.fishmaster.ms.be.pub.building.configuration.kafka.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfiguration {

    private final KafkaProperties kafkaProperties;

    private <V> HashMap<String, Object> getConfigs(Class<? extends Serializer<V>> serializerClass,
                                                   Class<? extends KafkaProducerInterceptor<V>> interceptorClass) {
        var configs = new HashMap<String, Object>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configs.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorClass.getName());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);

        if (kafkaProperties.getAuthentication().isEnable()) {
            configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getAuthentication().getSecurityProtocolConfig());
            configs.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.getAuthentication().getSaslMechanism());
            configs.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaProperties.getAuthentication().getSaslJaasConfig());
        }
        return configs;
    }

    @Bean
    public KafkaTemplate<String, byte[]> byteArrayKafkaTemplate() {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        getConfigs(ByteArraySerializer.class, KafkaByteArrayProducerInterceptor.class)
                )
        );
    }

}
