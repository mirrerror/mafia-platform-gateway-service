package md.faf223.mafiaplatformgatewayservice.services;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.*;
import md.faf223.mafiaplatformgatewayservice.exceptions.*;
import md.faf223.mafiaplatformgatewayservice.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;
    private final RestTemplate restTemplate;

    @Value("${services.user.url}")
    private String userServiceUrl;

    public void signUp(RegisterUserDto input) {
        try {
            restTemplate.postForObject(userServiceUrl + "/api/users/register", input, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new UserWithThisEmailAlreadyExistsException("A user with this email or username already exists");
            }
            throw new RuntimeException("An error occurred during sign up");
        }
    }

    public User authenticate(LoginUserDto input) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
        } catch (BadCredentialsException ignored) {
            throw new InvalidCredentialsException("Invalid username and/or password");
        }

        User user = userServiceClient.findByEmail(input.getUsername());

        return user;
    }

}