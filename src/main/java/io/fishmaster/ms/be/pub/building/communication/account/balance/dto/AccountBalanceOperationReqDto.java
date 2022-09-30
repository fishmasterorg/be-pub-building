package io.fishmaster.ms.be.pub.building.communication.account.balance.dto;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBalanceOperationReqDto {
    String accountId;
    OperationType operationType;
    Currency currency;
    Double quantity;
    AuditSource auditSource;
    AuditAction auditAction;

    public static AccountBalanceOperationReqDto of(
            String accountId, OperationType operationType, Currency currency, Double quantity, AuditSource auditSource,
            AuditAction auditAction) {
        return new AccountBalanceOperationReqDto(
                accountId, operationType, currency, quantity, auditSource, auditAction);
    }

    public static AccountBalanceOperationReqDto ofNegate(AccountBalanceOperationReqDto dto) {
        return of(
                dto.getAccountId(), dto.getOperationType().negate(), dto.getCurrency(), dto.getQuantity(),
                dto.getAuditSource(), dto.getAuditAction());
    }
}
