package org.facturepro.backoffice.shared.domain.exceptions;

/**
 * Violation de règle métier — mappée en HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(final String message) {
        super(message);
    }
}