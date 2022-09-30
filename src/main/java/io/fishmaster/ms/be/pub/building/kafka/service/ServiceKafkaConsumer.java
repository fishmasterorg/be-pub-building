package io.fishmaster.ms.be.pub.building.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.StringValue;

import io.fishmaster.ms.be.commons.constant.task.WeekDay;
import io.fishmaster.ms.be.commons.kafka.utility.KafkaParser;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.service.ConfigurationsStorageCommunicationService;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared.PubBuildingPreparedDailyTaskService;
import io.fishmaster.ms.be.pub.building.utility.KafkaMDCUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ServiceKafkaConsumer {

    private final ConfigurationsStorageCommunicationService configurationsStorageCommunicationService;

    private final PubBuildingPreparedDailyTaskService pubBuildingPreparedDailyTaskService;

    @KafkaListener(
            topics = "${kafka.topic.service-cache-evict}",
            groupId = "${kafka.config.group-id}",
            clientIdPrefix = "${kafka.config.client-id}-${kafka.topic.service-cache-evict}-${server.port}",
            containerFactory = "byteArrayConcurrentKafkaListenerContainerFactory",
            autoStartup = "${kafka.config.consumer.auto-startup:true}"
    )
    public void evictServiceCache(ConsumerRecord<String, byte[]> consumerRecord) {
        KafkaParser.parseFromOfNullable(consumerRecord.value(), StringValue.getDefaultInstance())
                .ifPresent(stringValue -> KafkaMDCUtility.onConsume(
                        consumerRecord,
                        () -> {
                            log.info("Consumed message for evict state cache");
                            configurationsStorageCommunicationService.clearState();
                            log.info("Processed message for evict state cache");
                        },
                        e -> log.error("Error while on consumed for evict state cache. Error = {}", e.getMessage())
                ));
    }

    @KafkaListener(
            topics = "${kafka.topic.service-prepared-daily-tasks-reload}",
            groupId = "${kafka.config.group-id}",
            clientIdPrefix = "${kafka.config.client-id}-${kafka.topic.service-prepared-daily-tasks-reload}-${server.port}",
            containerFactory = "byteArrayConcurrentKafkaListenerContainerFactory",
            autoStartup = "${kafka.config.consumer.auto-startup:true}"
    )
    public void reloadServicePreparedDailyTasks(ConsumerRecord<String, byte[]> consumerRecord) {
        KafkaParser.parseFromOfNullable(consumerRecord.value(), StringValue.getDefaultInstance())
                .ifPresent(stringValue -> {
                    var weekDay = WeekDay.valueOf(stringValue.getValue());

                    KafkaMDCUtility.onConsume(
                            consumerRecord,
                            () -> {
                                log.info("Consumed message for reload service prepared daily task. Week day = {}", weekDay);
                                pubBuildingPreparedDailyTaskService.prepareDailyTasksForWeekDay(weekDay);
                                log.info("Processed message for reload service prepared daily task. Week day = {}", weekDay);
                            },
                            e -> log.error("Error while on consumed for reload service prepared daily task. Week day = {}. Error = {}",
                                    weekDay, e.getMessage())
                    );
                });
    }

}
