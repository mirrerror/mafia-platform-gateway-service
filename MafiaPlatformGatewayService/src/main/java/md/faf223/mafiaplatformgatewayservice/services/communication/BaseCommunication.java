package md.faf223.mafiaplatformgatewayservice.services.communication;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public abstract class BaseCommunication {

    protected final WebClient webClient;
    private final String serviceName;

    protected BaseCommunication(String baseUrl, String port, String serviceName) {
        String fullBaseUrl = String.format("http://%s:%s", baseUrl, port);
        log.info("{} initialized with base URL: {}", serviceName, fullBaseUrl);
        this.webClient = WebClient.builder().baseUrl(fullBaseUrl).build();
        this.serviceName = serviceName;
    }

    protected <T> T makeGetRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        try {
            ApiResponse<T> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + uri);
        } catch (WebClientResponseException e) {
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePostRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        try {
            WebClient.RequestBodySpec requestSpec = webClient.post()
                    .uri(uri);

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec.bodyToMono(typeRef).block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + uri);
        } catch (WebClientResponseException e) {
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePutRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        try {
            WebClient.RequestBodySpec requestSpec = webClient.put()
                    .uri(uri);

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec.bodyToMono(typeRef).block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + uri);
        } catch (WebClientResponseException e) {
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T> T makeDeleteRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        try {
            ApiResponse<T> response = webClient.delete()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + uri);
        } catch (WebClientResponseException e) {
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }
}