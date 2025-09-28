package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.*;
import md.faf223.mafiaplatformgatewayservice.models.User;
import md.faf223.mafiaplatformgatewayservice.responses.LoginResponse;
import md.faf223.mafiaplatformgatewayservice.services.AuthenticationService;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterUserDto> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        authenticationService.signUp(registerUserDto);
        return ResponseEntity.ok(registerUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String token = jwtService.generateToken(authenticatedUser);
        long expiresIn = jwtService.getJwtExpirationTime();
        return ResponseEntity.ok(new LoginResponse(token, expiresIn));
    }

}