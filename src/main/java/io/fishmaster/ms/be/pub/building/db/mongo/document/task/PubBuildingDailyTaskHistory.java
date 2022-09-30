package io.fishmaster.ms.be.pub.building.db.mongo.document.task;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.model.result.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pub_building_daily_task_history")
public class PubBuildingDailyTaskHistory {
    @Id
    private ObjectId id;
    private String requestId;
    @Indexed(unique = true)
    private Long dailyTaskId;
    private String accountId;
    private Long buildingId;
    private Set<Long> characterIds;
    private String configurationId;
    private Integer currentProgress;
    private Integer finalProgress;
    private Result result;
    private Status status;
    private Long createdDate;
    private Long lastModifiedDate;
}
