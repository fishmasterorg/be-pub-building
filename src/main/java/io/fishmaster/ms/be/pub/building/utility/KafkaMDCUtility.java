package io.fishmaster.ms.be.pub.building.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaMDCUtility {

    public static <T> void onConsume(ConsumerRecord<String, T> record, Runnable runnable, Consumer<Exception> exceptionConsumer) {
        var header = record.headers().lastHeader(MDCUtility.X_TRACE_ID_HEADER);

        var headerValue = Optional.ofNullable(header)
                .map(Header::value)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .orElse("");

        MDCUtility.putTraceId(headerValue);
        try {
            runnable.run();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        } finally {
            MDCUtility.clear();
        }
    }

}
