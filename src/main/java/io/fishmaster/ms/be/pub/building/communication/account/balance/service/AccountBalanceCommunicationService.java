package io.fishmaster.ms.be.pub.building.communication.account.balance.service;

import java.util.function.Consumer;

import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationResDto;

public interface AccountBalanceCommunicationService {

    AccountBalanceOperationResDto handleOperation(AccountBalanceOperationReqDto reqDto);

    default AccountBalanceOperationResDto handleOperation(
            AccountBalanceOperationReqDto reqDto, Consumer<AccountBalanceOperationReqDto> consumer) {
        var resDto = handleOperation(reqDto);
        consumer.accept(reqDto);
        return resDto;
    }

}
