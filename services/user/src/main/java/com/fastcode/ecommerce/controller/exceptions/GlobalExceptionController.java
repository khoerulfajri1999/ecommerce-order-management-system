package com.fastcode.ecommerce.controller.exceptions;

import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<CommonResponse<String>> handleResourceNotFoundException(ResourceNotFoundException e){
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<CommonResponse<String>> handleIllegalArgumentException(IllegalArgumentException e){
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return CommonResponse.<Map<String, String>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .data(errors)
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String extractedMessage = extractUniqueConstraintMessage(e.getRootCause().getMessage());

        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(extractedMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String extractUniqueConstraintMessage(String dbMessage) {
        Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\) already exists");
        Matcher matcher = pattern.matcher(dbMessage);

        if (matcher.find()) {
            return matcher.group(1) + " " + matcher.group(2) + " already exists.";
        }
        return "Duplicate entry detected";
    }
}
