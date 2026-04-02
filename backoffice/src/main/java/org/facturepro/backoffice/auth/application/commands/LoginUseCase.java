package org.facturepro.backoffice.auth.application.commands;

import org.facturepro.backoffice.auth.domain.entities.User;
import org.facturepro.backoffice.auth.domain.repositories.UserRepository;
import org.facturepro.backoffice.auth.infrastructure.configs.JwtService;
import org.facturepro.backoffice.auth.web.dto.LoginResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Use Case : connexion — retourne un access token JWT + refresh token.
 */
@Service
@Transactional(readOnly = true)
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCase(
        final UserRepository userRepository,
        final PasswordEncoder passwordEncoder,
        final JwtService jwtService
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.jwtService = Objects.requireNonNull(jwtService);
    }

    public LoginResponse execute(final LoginCommand command) {
        final User user = userRepository.findByEmail(command.email())
            .filter(User::isActive)
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Identifiants invalides");
        }

        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(
            accessToken,
            refreshToken,
            user.getId(),
            user.getTenantId(),
            user.getEmail().getValue(),
            user.getFullName(),
            user.getRole().name()
        );
    }
}