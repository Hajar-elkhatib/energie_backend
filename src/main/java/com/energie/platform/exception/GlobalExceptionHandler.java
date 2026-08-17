package com.energie.platform.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class) ResponseEntity<Map<String,Object>> api(ApiException e){return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getMessage(),"status",e.getStatus().value()));}
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){
    Map<String,String> errors=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("message","Données invalides","errors",errors)); }
  @ExceptionHandler(Exception.class) ResponseEntity<Map<String,Object>> unexpected(Exception e){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","Erreur interne"));}
}
