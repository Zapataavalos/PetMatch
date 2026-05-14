package com.petmatch.msreporttype.exception;
import org.springframework.http.*; import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime; import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<Map<String,Object>> nf(ResourceNotFoundException ex){return b(HttpStatus.NOT_FOUND,ex.getMessage());}
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> conflict(IllegalArgumentException ex){return b(HttpStatus.CONFLICT,ex.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> val(MethodArgumentNotValidException ex){
        Map<String,String> errs=new HashMap<>();ex.getBindingResult().getAllErrors().forEach(e->errs.put(((FieldError)e).getField(),e.getDefaultMessage()));
        Map<String,Object> body=new HashMap<>();body.put("timestamp",LocalDateTime.now().toString());body.put("status",400);body.put("errors",errs);return ResponseEntity.badRequest().body(body);}
    private ResponseEntity<Map<String,Object>> b(HttpStatus s,String m){Map<String,Object> body=new HashMap<>();body.put("timestamp",LocalDateTime.now().toString());body.put("status",s.value());body.put("message",m);return ResponseEntity.status(s).body(body);}
}
