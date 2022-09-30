package io.fishmaster.ms.be.pub.building.configuration.kafka.interceptor;

import static io.fishmaster.ms.be.commons.constant.kafka.KafkaCustomHeaders.SERVICE_PRODUCER;
import static io.fishmaster.ms.be.commons.constant.kafka.KafkaCustomHeaders.SERVICE_TARGET;
import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.toServiceTargetHeaderValue;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;

public abstract class KafkaProducerInterceptor<T> implements ProducerInterceptor<String, T> {

    private static final ServiceName SERVICE_NAME = ServiceName.BE_PUB_BUILDING;

    @Override
    public ProducerRecord<String, T> onSend(ProducerRecord<String, T> record) {
        var key = Optional.ofNullable(record.key())
                .filter(StringUtils::isNotBlank)
                .orElse(UUID.randomUUID().toString());

        var headers = record.headers();
        headers.add(MDCUtility.X_TRACE_ID_HEADER, MDCUtility.getTraceIdBytes());
        headers.add(SERVICE_PRODUCER.getHeaderName(), SERVICE_NAME.name().getBytes(StandardCharsets.UTF_8));

        var header = headers.lastHeader(SERVICE_TARGET.getHeaderName());
        if (Objects.isNull(header) || header.value().length == 0) {
            headers.add(SERVICE_TARGET.getHeaderName(), toServiceTargetHeaderValue(ServiceName.values()).getBytes(StandardCharsets.UTF_8));
        }

        return new ProducerRecord<>(record.topic(), record.partition(), key, record.value(), headers);
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

}
