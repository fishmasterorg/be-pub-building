package io.fishmaster.ms.be.pub.building.utility;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.fishmaster.ms.be.commons.constant.kafka.KafkaCustomHeaders.SERVICE_TARGET;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaUtility {

    public static final String SERVICE_TARGET_DELIMITER = ";";

    public static <T> ProducerRecord<String, T> toRecord(String topic, String key, T value, Map<String, String> headers) {
        var producerRecord = new ProducerRecord<>(topic, null, key, value);
        var kafkaHeaders = producerRecord.headers();

        headers.forEach((headerKey, headerValue) -> kafkaHeaders.add(headerKey, headerValue.getBytes(StandardCharsets.UTF_8)));

        return producerRecord;
    }

    public static <T> ProducerRecord<String, T> toRecord(String topic, T value, Map<String, String> headers) {
        return toRecord(topic, null, value, headers);
    }

    public static <T> ProducerRecord<String, T> toRecord(String topic, String key, T value) {
        return toRecord(topic, key, value, Map.of());
    }

    public static <T> RecordFilterStrategy<String, T> getTargetServiceRecordFilterStrategy(ServiceName serviceName) {
        return consumerRecord -> {
            var targetHeader = consumerRecord.headers().lastHeader(SERVICE_TARGET.getHeaderName());
            if (Objects.isNull(targetHeader) || targetHeader.value().length == 0) {
                return true;
            }
            var targets = new String(targetHeader.value(), StandardCharsets.UTF_8).split(SERVICE_TARGET_DELIMITER);
            var targetServiceNames = Stream.of(targets)
                    .map(ServiceName::valueOf)
                    .toList();

            return !targetServiceNames.contains(serviceName);
        };
    }

    public static String toServiceTargetHeaderValue(ServiceName... serviceNames) {
        return Stream.of(serviceNames)
                .map(ServiceName::name)
                .collect(Collectors.joining(SERVICE_TARGET_DELIMITER));
    }

}
