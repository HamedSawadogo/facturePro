package org.facturepro.backoffice.auth.application.commands;

import org.facturepro.backoffice.auth.domain.entities.User;
import org.facturepro.backoffice.auth.domain.repositories.UserRepository;
import org.facturepro.backoffice.shared.domain.exceptions.BusinessRuleViolationException;
import org.facturepro.backoffice.shared.domain.valueObjects.Email;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Use Case : inscription d'un nouvel utilisateur.
 * 1 Use Case = 1 classe.
 */
@Service
@Transactional
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(
        final UserRepository userRepository,
        final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    public ResourceCreatedId execute(final RegisterUserCommand command) {
        final Email email = Email.of(command.email());

        if (userRepository.existsByEmailAndTenantId(email.getValue(), command.tenantId())) {
            throw new BusinessRuleViolationException(
                "Un utilisateur avec cet email existe déjà: " + command.email()
            );
        }

        final User user = User.create(
            command.tenantId(),
            email,
            passwordEncoder.encode(command.password()),
            command.firstName(),
            command.lastName(),
            command.role()
        );

        final User saved = userRepository.save(user);
        return ResourceCreatedId.of(saved.getId());
    }
}