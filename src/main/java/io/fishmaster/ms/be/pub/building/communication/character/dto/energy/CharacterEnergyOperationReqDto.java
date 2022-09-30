package io.fishmaster.ms.be.pub.building.communication.character.dto.energy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.operation.OperationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterEnergyOperationReqDto {
    String accountId;
    List<Operation> operations;
    AuditSource auditSource;
    AuditAction auditAction;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Operation {
        Long characterId;
        OperationType operationType;
        Double quantity;

        public static Operation of(Long characterId, OperationType type, Double quantity) {
            return new Operation(characterId, type, quantity);
        }
    }

    public static CharacterEnergyOperationReqDto of(
            String accountId, List<Operation> operations, AuditSource auditSource, AuditAction auditAction) {
        return new CharacterEnergyOperationReqDto(accountId, operations, auditSource, auditAction);
    }

    public static CharacterEnergyOperationReqDto of(
            String accountId, Long characterId, OperationType type, Double quantity, AuditSource auditSource, AuditAction auditAction) {
        var operation = Operation.of(characterId, type, quantity);
        return of(accountId, List.of(operation), auditSource, auditAction);
    }

    public static CharacterEnergyOperationReqDto ofNegate(CharacterEnergyOperationResDto dto) {
        var operations = dto.getOperations().stream()
                .map(operation -> {
                    var quantity = operation.getQuantityBeforeUpdate() - operation.getQuantityAfterUpdate();
                    return Operation.of(
                            operation.getCharacterId(), operation.getOperationType().negate(),
                            Math.abs(quantity));
                })
                .toList();

        return of(dto.getAccountId(), operations, dto.getAuditSource(), dto.getAuditAction());
    }
}
