package io.fishmaster.ms.be.pub.building.communication.city.balance.service;

import java.util.List;
import java.util.function.Consumer;

import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationResDto;

public interface CityBalanceCommunicationService {

    CityBalanceOperationResDto handleOperation(CityBalanceOperationReqDto reqDto);

    default CityBalanceOperationResDto handleOperation(
            CityBalanceOperationReqDto reqDto, Consumer<CityBalanceOperationReqDto> consumer) {
        var resDto = handleOperation(reqDto);
        consumer.accept(reqDto);
        return resDto;
    }

    List<CityBalanceOperationResDto> handleOperationBatch(List<CityBalanceOperationReqDto> reqDtos);

    default List<CityBalanceOperationResDto> handleOperationBatch(
            List<CityBalanceOperationReqDto> reqDtos, Consumer<List<CityBalanceOperationReqDto>> consumer) {
        var resDtos = handleOperationBatch(reqDtos);
        consumer.accept(reqDtos);
        return resDtos;
    }

}
