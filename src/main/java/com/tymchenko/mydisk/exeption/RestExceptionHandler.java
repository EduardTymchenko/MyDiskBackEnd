package com.tymchenko.mydisk.exeption;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<ApiError> handleFileNotFoundException(FileNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
        apiError.setMessage("Файл не найден по " + ex.getMessage() );
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(FolderNotFoundException.class)
    protected ResponseEntity<ApiError> handleFolderNotFoundException(FolderNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
        apiError.setMessage("Папка не найдена по " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage("Ошибка авторизации.\nАвторизируйтесь снова");
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateNameException.class)
    protected ResponseEntity<ApiError> handleDuplicateNameException(DuplicateNameException ex) {
        StringBuilder errMess = new StringBuilder();
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        if (ex.getTypeObject().equals("folder")) errMess.append("Папка с именем \"");
        if (ex.getTypeObject().equals("file")) errMess.append("Файл с именем \"");
        errMess.append(ex.getName()).append("\" уже существует!");
        apiError.setMessage(errMess.toString());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status, "method arg not valid", ex);
//        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        return new ResponseEntity<Object>(apiError, HttpStatus.BAD_REQUEST);
    }

//        @ExceptionHandler({ AccessDeniedException.class })
//    public ResponseEntity<Object> handleAccessDeniedException(
//            Exception ex, WebRequest request) {
//        return new ResponseEntity<Object>(
//                "Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
//    }


//    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
//    protected ResponseEntity<AwesomeException> handleConflict(
//            RuntimeException ex, WebRequest request) {
//
//        return new ResponseEntity<>(new AwesomeException("illll"), HttpStatus.CONFLICT);
//    }



}