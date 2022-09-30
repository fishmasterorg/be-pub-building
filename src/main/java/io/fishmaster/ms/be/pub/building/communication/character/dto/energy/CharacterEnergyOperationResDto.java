package io.fishmaster.ms.be.pub.building.communication.character.dto.energy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.operation.OperationType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterEnergyOperationResDto {
    String accountId;
    List<Operation> operations;
    AuditSource auditSource;
    AuditAction auditAction;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Operation {
        Long characterId;
        OperationType operationType;

        Double quantityBeforeUpdate;
        Double quantityAfterUpdate;
    }
}
