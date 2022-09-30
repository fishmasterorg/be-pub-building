package io.fishmaster.ms.be.pub.building.communication.account.self.service;

import io.fishmaster.ms.be.pub.building.communication.account.self.dto.AccountDto;

public interface AccountCommunicationService {

    AccountDto getById(String id);

}
