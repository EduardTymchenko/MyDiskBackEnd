package com.tymchenko.mydisk.exeption;


import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private Environment env;

    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<ApiError> handleFileNotFoundException(FileNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
        apiError.setMessage("Файл не найден по " + ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ApiError> handleFolderNotFoundException(FolderNotFoundException ex) {
        String mess = "Папка не найдена по " +  ex.getMessage();
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
        apiError.setMessage(mess);
        apiError.setTodo("reloadRoot");
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage("Ошибка авторизации.\nАвторизируйтесь снова");
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ApiError> handleDuplicateNameException(DuplicateNameException ex) {
        StringBuilder errMess = new StringBuilder();
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        if (ex.getTypeObject().equals("folder")) errMess.append("Папка с именем \"");
        if (ex.getTypeObject().equals("file")) errMess.append("Файл с именем \"");
        errMess.append(ex.getName()).append("\" уже существует!");
        apiError.setMessage(errMess.toString());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка в типе параметра");
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();
        String message = String.format("Параметр '%s' должен быть '%s', а передан '%s'", name, type, value);
        apiError.setMessage(message);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String mess = "Параметр" + " \"" + ex.getParameterName() + "\" " + "отсутствует в запросе";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка параметров запроса");
        apiError.setMessage(mess);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY,"Превышен размер загрузки");

        if(exc.getCause().getCause() instanceof FileUploadBase.SizeLimitExceededException){
            apiError.setMessage("Суммарный размер файлов > " +
                    env.getProperty("spring.servlet.multipart.max-request-size"));
        }
        if(exc.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException){
            apiError.setMessage("Размер загружаемого файла > " +
                    env.getProperty("spring.servlet.multipart.max-file-size"));
        }
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(JDBCException.class)
    public ResponseEntity<ApiError> handleSQLException(JDBCException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY,"Ошибка Базы Данных");
            apiError.setMessage(ex.getSQLException().getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}