package com.energie.platform.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String,Object>> api(ApiException e){
    return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getMessage(), "status", e.getStatus().value()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){
    Map<String,String> errors = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors().forEach(x -> errors.put(x.getField(), x.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("message", "Données invalides", "errors", errors));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Map<String,Object>> notFound(NoResourceFoundException e){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Ressource non trouvée : /" + e.getResourcePath()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> unexpected(Exception e){
    e.printStackTrace();
    String msg = e.getMessage() != null ? e.getMessage() : "Erreur interne";
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", msg));
  }
}
