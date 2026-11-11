package com.miguel.wealth_orders.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CanNotBeExecutedException.class)
    public ProblemDetail handleCanNotBeExecuted(CanNotBeExecutedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PreconditionException.class)
    public ProblemDetail handlePreconditionException(PreconditionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_FAILED, exception.getMessage());
    }

    @ExceptionHandler(PreconditionRequiredException.class)
    public ProblemDetail handlePreconditionRequiredException(PreconditionRequiredException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_REQUIRED, exception.getMessage());
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // I didn't write this code because I wasn't understanding what should I do to mitigate the point M6.
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        // Se o mesmo campo tiver duas regras falhadas, junta as mensagens
                        (existingMessage, newMessage) -> existingMessage + "; " + newMessage
                ));

        // 2. Criar o ProblemDetail padrão do Spring
        ProblemDetail problem = createProblemDetail(
                ex,
                status,
                "One or more fields failed validation.",
                null,
                null,
                request
        );

        // 3. Adicionar a propriedade customizada "errors" pedida na revisão
        problem.setProperty("errors", fieldErrors);

        return handleExceptionInternal(ex, problem, headers, status, request);
    }
}
