package md.faf223.mafiaplatformgatewayservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    /**
     * RestTemplate for user management service
     * This template will forward tokens for internal authentication between gateway and user management service
     */
    @Bean(name = "userManagementRestTemplate")
    public RestTemplate userManagementRestTemplate() {
        return new RestTemplate();
    }

    /**
     * RestTemplate for other downstream services
     * This template strips Authorization headers before forwarding requests
     */
    @Bean(name = "downstreamServicesRestTemplate")
    public RestTemplate downstreamServicesRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderStrippingInterceptor());
        restTemplate.setInterceptors(interceptors);
        
        return restTemplate;
    }

    /**
     * Interceptor that removes Authorization headers before forwarding to downstream services
     */
    private static class HeaderStrippingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, 
                byte[] body, 
                ClientHttpRequestExecution execution
        ) throws IOException {
            // Remove Authorization header to prevent forwarding to downstream services
            request.getHeaders().remove("Authorization");
            return execution.execute(request, body);
        }
    }
}
