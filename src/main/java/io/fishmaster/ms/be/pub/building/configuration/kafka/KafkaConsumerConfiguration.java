package io.fishmaster.ms.be.pub.building.configuration.kafka;

import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.getTargetServiceRecordFilterStrategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.pub.building.configuration.kafka.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    private static final ServiceName SERVICE_NAME = ServiceName.BE_PUB_BUILDING;

    private final KafkaProperties kafkaProperties;

    private <V> Map<String, Object> getConfigs(Class<? extends Deserializer<V>> deserializerClass) {
        var configs = new HashMap<String, Object>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConfig().getGroupId());
        configs.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getConfig().getClientId());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getConfig().getEnableAutoCommit());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializerClass);

        if (kafkaProperties.getAuthentication().isEnable()) {
            configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getAuthentication().getSecurityProtocolConfig());
            configs.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.getAuthentication().getSaslMechanism());
            configs.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaProperties.getAuthentication().getSaslJaasConfig());
        }
        return configs;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> byteArrayConcurrentKafkaListenerContainerFactory() {
        var configs = getConfigs(ByteArrayDeserializer.class);

        var kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(configs);

        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.setRecordFilterStrategy(getTargetServiceRecordFilterStrategy(SERVICE_NAME));

        return factory;
    }

}
