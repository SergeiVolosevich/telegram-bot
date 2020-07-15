package by.resliv.traveladvisor.controller;

import by.resliv.traveladvisor.exception.UniqueConstraintException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String FAILED_TO_EXECUTE_METHOD_CAUSE = "Failed to execute method. Cause: ";
    private static final String METHOD_NOT_SUPPORTED_MSG = "Method {0} not supported. Supported methods: {1}";
    private static final String NO_HANDLER_FOUND_FOR = "No handler found for {0} {1}";
    private static final String MALFORMED_JSON_REQUEST = "Malformed JSON request";
    private static final String ERROR_OCCURRED = "Internal error occurred";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, errors, path);
        return new ResponseEntity<>(response, headers, status);
    }


    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                         WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, errors, path);
        return handleExceptionInternal(ex, response, headers, status, request);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String contentType = Objects.requireNonNull(ex.getContentType()).getType();
        String supportedMediaTypes = ex.getSupportedMediaTypes().stream()
                .map(MimeType::getType)
                .collect(Collectors.joining(", "));
        String msg = MessageFormat.format("{0} media type is not supported. Supported media types are {1}",
                contentType, supportedMediaTypes);
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(msg), path);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<Object> handleUnsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException ex, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(ex.getMessage()), path);
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        final String error = MessageFormat.format("{0} parameter is missing.", ex.getParameterName());
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(error), path);
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(HttpStatus.BAD_REQUEST, errors, path);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(HttpStatus.NOT_FOUND,
                Collections.singletonList(ex.getMessage()), path);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UniqueConstraintException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(UniqueConstraintException ex, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(HttpStatus.NOT_FOUND,
                Collections.singletonList(ex.getMessage()), path);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String errorMessage = MessageFormat.format(NO_HANDLER_FOUND_FOR, ex.getHttpMethod(), ex.getRequestURL());
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(errorMessage), path);
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(ERROR_OCCURRED), path);
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String path = request.getDescription(false);
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(MALFORMED_JSON_REQUEST),
                path);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status,
                                                                         WebRequest request) {
        log.error(FAILED_TO_EXECUTE_METHOD_CAUSE, ex);
        String unsupportedMethodName = ex.getMethod();
        StringJoiner joiner = new StringJoiner(", ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(method -> joiner.add(method.name()));
        String msg = MessageFormat.format(METHOD_NOT_SUPPORTED_MSG, unsupportedMethodName, joiner);
        String path = request.getDescription(false);
        headers.setAllow(ex.getSupportedHttpMethods());
        ApiErrorResponse response = buildResponseMessage(status, Collections.singletonList(msg), path);
        return new ResponseEntity<>(response, headers, status);
    }

    private ApiErrorResponse buildResponseMessage(HttpStatus status, List<String> errors, String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDate.now().toString())
                .path(path)
                .status(status.getReasonPhrase())
                .code(status.value())
                .messages(errors)
                .build();
    }
}
