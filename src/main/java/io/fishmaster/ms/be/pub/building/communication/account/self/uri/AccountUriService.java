package io.fishmaster.ms.be.pub.building.communication.account.self.uri;

import java.net.URI;

public interface AccountUriService {

    URI getByParamUri(String key, String value);

}
