package md.faf223.mafiaplatformgatewayservice.services.communication; // Assuming a services package

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.AddItemDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDataDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;
import md.faf223.mafiaplatformgatewayservice.responses.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class ItemServiceCommunication {

    private final WebClient webClient;

    public ItemServiceCommunication(@Value("${CHARACTER_SERVICE_HOST}") String baseUrl,
                                     @Value("${CHARACTER_SERVICE_PORT}") String port) {

        String fullBaseUrl = String.format("http://%s:%s", baseUrl, port);
        log.info("ItemServiceCommunication initialized with base URL: {}", fullBaseUrl);

        this.webClient = WebClient.builder()
                .baseUrl(fullBaseUrl)
                .build();

        log.info("WebClient created with base URL: {}", fullBaseUrl);
    }

    public List<ItemDto> getItemsForPlayer(Long playerId) {
        String uri = "/" + playerId + "/items";

        ParameterizedTypeReference<ApiResponse<ItemsResponse>> typeRef = new ParameterizedTypeReference<>() {
        };

        ApiResponse<ItemsResponse> apiResponse = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(typeRef) // Deserialize into the wrapper
                .block();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData().getItems();
        }

        throw new RuntimeException("Failed to retrieve items for player: " + playerId);
    }

    public ItemDataDto addItemToPlayer(Long playerId, AddItemDto itemDto) {
        String uri = "/" + playerId + "/items";
        AddItemDto request = new AddItemDto(itemDto.getItemId(), itemDto.getQuantity());

        ParameterizedTypeReference<ApiResponse<ItemDataDto>> typeRef = new ParameterizedTypeReference<>() {};

        ApiResponse<ItemDataDto> apiResponse = webClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }

        throw new RuntimeException("Failed to add item to player: " + playerId);
    }

    public ItemRemovedResponse removeItemFromPlayer(Long playerId, Long itemId) {
        String uri = String.format("/%d/items/%d", playerId, itemId);
        log.info("DELETE request to: {}", uri);

        ParameterizedTypeReference<ApiResponse<ItemRemovedResponse>> typeRef = new ParameterizedTypeReference<>() {};

        try {
            ApiResponse<ItemRemovedResponse> apiResponse = webClient.delete()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block();

            log.info("Response received: {}", apiResponse);

            if (apiResponse != null && apiResponse.getData() != null) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            log.error("Error removing item", e);
            throw e;
        }

        throw new RuntimeException("Failed to remove item " + itemId + " from player " + playerId);
    }

    public ItemUsedResponse useItem(Long playerId, Long itemId) {
        String uri = String.format("/%d/items/%d/use", playerId, itemId);

        ParameterizedTypeReference<ApiResponse<ItemUsedResponse>> typeRef = new ParameterizedTypeReference<>() {
        };

        ApiResponse<ItemUsedResponse> apiResponse = webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }

        throw new RuntimeException("Failed to use item " + itemId + " for player " + playerId);
    }
}