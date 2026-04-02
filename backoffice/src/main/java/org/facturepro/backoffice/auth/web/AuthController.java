package org.facturepro.backoffice.auth.web;

import jakarta.validation.Valid;
import org.facturepro.backoffice.auth.application.commands.LoginCommand;
import org.facturepro.backoffice.auth.application.commands.LoginUseCase;
import org.facturepro.backoffice.auth.application.commands.RegisterUserCommand;
import org.facturepro.backoffice.auth.application.commands.RegisterUserUseCase;
import org.facturepro.backoffice.auth.web.dto.LoginRequest;
import org.facturepro.backoffice.auth.web.dto.LoginResponse;
import org.facturepro.backoffice.auth.web.dto.RegisterRequest;
import org.facturepro.backoffice.shared.web.ResourceCreatedId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller Auth — endpoints publics de connexion et inscription.
 */
@RestController
@RequestMapping("/api/v1/auth")
public final class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(
        final RegisterUserUseCase registerUserUseCase,
        final LoginUseCase loginUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreatedId register(@Valid @RequestBody final RegisterRequest request) {
        return registerUserUseCase.execute(new RegisterUserCommand(
            request.tenantId(),
            request.email(),
            request.password(),
            request.firstName(),
            request.lastName(),
            request.role()
        ));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody final LoginRequest request) {
        return loginUseCase.execute(new LoginCommand(request.email(), request.password()));
    }
}