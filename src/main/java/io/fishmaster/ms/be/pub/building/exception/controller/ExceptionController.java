package io.fishmaster.ms.be.pub.building.exception.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.fishmaster.ms.be.commons.exception.RemoteServicePassThroughException;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.exception.model.ServiceErrorDto;
import io.fishmaster.ms.be.pub.building.exception.utility.ExceptionControllerLogger;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceErrorDto> handleServiceException(ServiceException e, HttpServletRequest request) {
        ExceptionControllerLogger.log(request, e);

        var errorDto = ServiceErrorDto.of(e.getCode(), e.getMessage());
        return new ResponseEntity<>(errorDto, new LinkedMultiValueMap<>(), errorDto.getHttpStatusCode());
    }

    @ExceptionHandler(RemoteServicePassThroughException.class)
    public ResponseEntity<ServiceErrorDto> handleRemoteServicePassThroughException(RemoteServicePassThroughException e,
                                                                                   HttpServletRequest request) {
        ExceptionControllerLogger.log(request, e);

        var errorDto = e.getServiceErrorDto();
        return new ResponseEntity<>(errorDto, new LinkedMultiValueMap<>(), errorDto.getHttpStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServiceErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                 HttpServletRequest request) {
        ExceptionControllerLogger.log(request, e);

        var errorDto = ServiceErrorDto.of(ExceptionCode.INNER_SERVICE, e.getMessage());
        return new ResponseEntity<>(errorDto, new LinkedMultiValueMap<>(), errorDto.getHttpStatusCode());
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ServiceErrorDto> handleServletException(ServletException e, HttpServletRequest request) {
        ExceptionControllerLogger.log(request, e);

        var errorDto = ServiceErrorDto.of(ExceptionCode.INNER_SERVICE, e.getMessage());
        return new ResponseEntity<>(errorDto, new LinkedMultiValueMap<>(), errorDto.getHttpStatusCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServiceErrorDto> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        ExceptionControllerLogger.log(request, e);

        var errorDto = ServiceErrorDto.of(ExceptionCode.INNER_SERVICE, e.getMessage());
        return new ResponseEntity<>(errorDto, new LinkedMultiValueMap<>(), errorDto.getHttpStatusCode());
    }

}
