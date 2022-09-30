package io.fishmaster.ms.be.pub.building.service.account;

import io.fishmaster.ms.be.commons.kafka.dto.account.KafkaAccount;

public interface AccountService {

    void init(KafkaAccount kafkaAccount);

    void remove(KafkaAccount kafkaAccount);

}
