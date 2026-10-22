package jp.shiftcheck.controller;
import jp.shiftcheck.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String,Object> notFound(ResourceNotFoundException ex){return body(404,ex.getMessage());}
  @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> validation(MethodArgumentNotValidException ex){
    String msg=ex.getBindingResult().getFieldErrors().stream()
      .map(e->e.getField()+": "+e.getDefaultMessage()).collect(Collectors.joining("; "));
    return body(400,msg);
  }
  @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> malformed(Exception ex){return body(400,"リクエスト形式または enum 値が正しくありません");}
  @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> badRequest(IllegalArgumentException ex){return body(400,ex.getMessage());}
  private Map<String,Object> body(int status,String message){
    Map<String,Object> m=new LinkedHashMap<>(); m.put("status",status);
    m.put("error",HttpStatus.valueOf(status).getReasonPhrase()); m.put("message",message); return m;
  }
}
