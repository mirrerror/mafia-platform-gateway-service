package md.faf223.mafiaplatformgatewayservice.services;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user.url}")
    private String userServiceUrl;

    public User findByEmail(String email) {
        String url = userServiceUrl + "/api/users/email/" + email;
        return restTemplate.getForObject(url, User.class);
    }
}