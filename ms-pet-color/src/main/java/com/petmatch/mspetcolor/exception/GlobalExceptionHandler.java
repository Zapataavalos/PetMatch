package com.petmatch.mspetcolor.exception;
import feign.FeignException;
import org.springframework.http.*; import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime; import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException ex){return build(HttpStatus.NOT_FOUND,ex.getMessage());}
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleConflict(IllegalArgumentException ex){return build(HttpStatus.CONFLICT,ex.getMessage());}
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String,Object>> handleFeign(FeignException ex){return build(HttpStatus.SERVICE_UNAVAILABLE,"Microservicio no disponible: "+ex.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e->errors.put(((FieldError)e).getField(),e.getDefaultMessage()));
        Map<String,Object> body=new HashMap<>(); body.put("timestamp",LocalDateTime.now().toString()); body.put("status",400); body.put("errors",errors);
        return ResponseEntity.badRequest().body(body);
    }
    private ResponseEntity<Map<String,Object>> build(HttpStatus s,String m){
        Map<String,Object> b=new HashMap<>(); b.put("timestamp",LocalDateTime.now().toString()); b.put("status",s.value()); b.put("message",m);
        return ResponseEntity.status(s).body(b);
    }
}
