package io.fishmaster.ms.be.pub.building.kafka.account;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.TextFormat;

import io.fishmaster.ms.be.commons.kafka.dto.account.KafkaAccount;
import io.fishmaster.ms.be.commons.kafka.utility.KafkaParser;
import io.fishmaster.ms.be.pub.building.service.account.AccountService;
import io.fishmaster.ms.be.pub.building.utility.KafkaMDCUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccountKafkaConsumer {

    private final AccountService accountService;

    @KafkaListener(
            topics = "${kafka.topic.account-init}",
            groupId = "${kafka.config.group-id}",
            clientIdPrefix = "${kafka.config.client-id}-${kafka.topic.account-init}-${server.port}",
            containerFactory = "byteArrayConcurrentKafkaListenerContainerFactory",
            autoStartup = "${kafka.config.consumer.auto-startup:true}"
    )
    public void init(ConsumerRecord<String, byte[]> consumerRecord) {
        KafkaParser.parseFromOfNullable(consumerRecord.value(), KafkaAccount.getDefaultInstance())
                .ifPresent(kafkaAccount -> {
                    var kafkaAccountLogInfo = TextFormat.printer().shortDebugString(kafkaAccount);

                    KafkaMDCUtility.onConsume(
                            consumerRecord,
                            () -> {
                                log.info("Consumed message for init account: [{}]", kafkaAccountLogInfo);
                                accountService.init(kafkaAccount);
                                log.info("Processed message for init account: [{}]", kafkaAccountLogInfo);
                            },
                            e -> log.error("Error while on consumed for init account: [{}]. Error = {}",
                                    kafkaAccountLogInfo, e.getMessage())
                    );
                });
    }

    @KafkaListener(
            topics = "${kafka.topic.account-remove}",
            groupId = "${kafka.config.group-id}",
            clientIdPrefix = "${kafka.config.client-id}-${kafka.topic.account-remove}-${server.port}",
            containerFactory = "byteArrayConcurrentKafkaListenerContainerFactory",
            autoStartup = "${kafka.config.consumer.auto-startup:true}"
    )
    public void remove(ConsumerRecord<String, byte[]> consumerRecord) {
        KafkaParser.parseFromOfNullable(consumerRecord.value(), KafkaAccount.getDefaultInstance())
                .ifPresent(kafkaAccount -> {
                    var kafkaAccountLogInfo = TextFormat.printer().shortDebugString(kafkaAccount);

                    KafkaMDCUtility.onConsume(
                            consumerRecord,
                            () -> {
                                log.info("Consumed message for remove account: [{}]", kafkaAccountLogInfo);
                                accountService.remove(kafkaAccount);
                                log.info("Processed message for remove account: [{}]", kafkaAccountLogInfo);
                            },
                            e -> log.error("Error while on consumed for remove account: [{}]. Error = {}",
                                    kafkaAccountLogInfo, e.getMessage())
                    );
                });
    }

}
