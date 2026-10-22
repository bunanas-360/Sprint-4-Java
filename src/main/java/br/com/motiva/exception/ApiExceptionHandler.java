package br.com.motiva.exception;

import br.com.motiva.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> tratarNaoEncontrado(ResourceNotFoundException ex,
                                                         HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> detalhes = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                detalhes.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, "A requisição contém campos inválidos.", detalhes);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> tratarConstraint(ConstraintViolationException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + ex.getMessage(), Map.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> tratarRequisicaoInvalida(Exception ex) {
        String message = ex instanceof HttpMessageNotReadableException
                ? "JSON inválido ou incompatível com o contrato da API."
                : ex.getMessage();
        return resposta(HttpStatus.BAD_REQUEST, message, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> tratarIntegridade(DataIntegrityViolationException ex) {
        return resposta(HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade do banco de dados.", Map.of());
    }

    private ResponseEntity<ApiError> resposta(HttpStatus status, String mensagem,
                                               Map<String, String> detalhes) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(body);
    }
}
