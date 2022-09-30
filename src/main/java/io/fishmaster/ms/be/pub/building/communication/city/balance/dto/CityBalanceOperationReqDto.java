package io.fishmaster.ms.be.pub.building.communication.city.balance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.fishmaster.ms.be.commons.constant.Currency;
import io.fishmaster.ms.be.commons.constant.audit.AuditAction;
import io.fishmaster.ms.be.commons.constant.audit.AuditSource;
import io.fishmaster.ms.be.commons.constant.operation.OperationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityBalanceOperationReqDto {
    Long cityId;
    String operationId;
    OperationType operationType;
    Currency currency;
    Double quantity;
    AuditSource auditSource;
    AuditAction auditAction;

    public static CityBalanceOperationReqDto of(Long cityId, OperationType operationType,
                                                Currency currency, Double quantity, AuditSource auditSource,
                                                AuditAction auditAction) {
        var operationId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return new CityBalanceOperationReqDto(
                cityId, operationId, operationType, currency, quantity, auditSource, auditAction
        );
    }

    public static CityBalanceOperationReqDto ofNegate(CityBalanceOperationReqDto dto) {
        return of(
                dto.getCityId(), dto.getOperationType().negate(), dto.getCurrency(), dto.getQuantity(),
                dto.getAuditSource(), dto.getAuditAction()
        );
    }
}
