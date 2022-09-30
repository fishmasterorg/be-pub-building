package io.fishmaster.ms.be.pub.building.exception.utility;

import io.fishmaster.ms.be.commons.exception.RemoteServicePassThroughException;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionControllerLogger {

    private static final String EXCEPTION_NAME_PREFIX = " | Exception: ";
    private static final String MESSAGE_PREFIX = " | Message: ";

    public static void log(HttpServletRequest request, ServiceException exception) {
        var message = prepareErrorMessage(request, exception, exception.getCode());
        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == exception.getCode().getHttpStatusCode()) {
            log.error(message);
        } else {
            log.warn(message);
        }
    }

    public static void log(HttpServletRequest request, RemoteServicePassThroughException exception) {
        var message = prepareErrorMessage(request, exception);
        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == exception.getServiceErrorDto().getHttpStatusCode()) {
            log.error(message);
        } else {
            log.warn(message);
        }
    }

    public static void log(HttpServletRequest request, Exception exception) {
        log.error(prepareErrorMessage(request, exception));
    }

    private static String prepareErrorMessage(HttpServletRequest request, Exception e) {
        return request.getRequestURI() + "?" + request.getQueryString() +
                EXCEPTION_NAME_PREFIX + e.getClass().getName() +
                MESSAGE_PREFIX + e.getMessage();
    }

    private static String prepareErrorMessage(HttpServletRequest request, Exception e, ExceptionCode exceptionCode) {
        return request.getRequestURI() + "?" + request.getQueryString() +
                EXCEPTION_NAME_PREFIX + e.getClass().getName() + ":" + exceptionCode.name() +
                MESSAGE_PREFIX + e.getMessage();
    }

}
