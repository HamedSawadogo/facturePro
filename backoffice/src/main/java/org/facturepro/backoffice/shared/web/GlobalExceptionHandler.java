package org.facturepro.backoffice.shared.web;

import org.facturepro.backoffice.shared.domain.exceptions.BusinessRuleViolationException;
import org.facturepro.backoffice.shared.domain.exceptions.IdempotencyException;
import org.facturepro.backoffice.shared.domain.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global d'exceptions — format RFC 9457 Problem Detail.
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(final ResourceNotFoundException ex) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRule(final BusinessRuleViolationException ex) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create("/errors/business-rule-violation"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(IdempotencyException.class)
    public ProblemDetail handleIdempotency(final IdempotencyException ex) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("/errors/duplicate-request"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalide"));
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation échouée");
        problem.setType(URI.create("/errors/validation"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(final BadCredentialsException ex) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        problem.setType(URI.create("/errors/unauthorized"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(final AccessDeniedException ex) {
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Accès refusé");
        problem.setType(URI.create("/errors/forbidden"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(final Exception ex) {
        log.error("Erreur inattendue", ex);
        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
        problem.setType(URI.create("/errors/internal"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}