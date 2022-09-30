package io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.TextFormat;

import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;
import io.fishmaster.ms.be.commons.kafka.utility.KafkaParser;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.PubBuildingDailyTaskProgressService;
import io.fishmaster.ms.be.pub.building.utility.KafkaMDCUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubBuildingDailyTaskKafkaConsumer {

    private final PubBuildingDailyTaskProgressService pubBuildingDailyTaskProgressService;

    @KafkaListener(
            topics = "${kafka.topic.pub-building-daily-task-progress-refresh}",
            groupId = "${kafka.config.group-id}",
            clientIdPrefix = "${kafka.config.client-id}-${kafka.topic.pub-building-daily-task-progress-refresh}-${server.port}",
            containerFactory = "byteArrayConcurrentKafkaListenerContainerFactory",
            autoStartup = "${kafka.config.consumer.auto-startup:true}"
    )
    public void init(ConsumerRecord<String, byte[]> consumerRecord) {
        KafkaParser.parseFromOfNullable(consumerRecord.value(), KafkaPubBuildingDailyTaskProgress.getDefaultInstance())
                .ifPresent(kafkaPubBuildingDailyTaskProgress -> {
                    var kafkaPubBuildingDailyTaskProgressLogInfo = TextFormat.printer().shortDebugString(kafkaPubBuildingDailyTaskProgress);

                    KafkaMDCUtility.onConsume(
                            consumerRecord,
                            () -> {
                                log.info("Consumed message for refresh pub building daily task progress: [{}]",
                                        kafkaPubBuildingDailyTaskProgressLogInfo);
                                pubBuildingDailyTaskProgressService.refreshTaskProgress(kafkaPubBuildingDailyTaskProgress);
                                log.info("Processed message for refresh pub building daily task progress: [{}]",
                                        kafkaPubBuildingDailyTaskProgressLogInfo);
                            },
                            e -> log.error("Error while on consumed for refresh pub building daily task progress: [{}]. Error = {}",
                                    kafkaPubBuildingDailyTaskProgressLogInfo, e.getMessage())
                    );
                });
    }

}
