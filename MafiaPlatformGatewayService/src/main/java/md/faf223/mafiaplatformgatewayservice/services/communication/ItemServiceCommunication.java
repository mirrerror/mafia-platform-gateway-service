package md.faf223.mafiaplatformgatewayservice.services.communication; // Assuming a services package

import md.faf223.mafiaplatformgatewayservice.dtos.AddItemDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDataDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;
import md.faf223.mafiaplatformgatewayservice.responses.ItemRemovedResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemUsedResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceCommunication extends BaseCommunication {

    public ItemServiceCommunication(@Value("${CHARACTER_SERVICE_HOST}") String baseUrl,
                                    @Value("${CHARACTER_SERVICE_PORT}") String port) {
        super(baseUrl, port, "ItemServiceCommunication");
    }

    public List<ItemDto> getItemsForPlayer(Long playerId) {
        ItemsResponse response = makeGetRequest(
                String.format("/%d/items", playerId),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getItems();
    }

    public ItemDataDto addItemToPlayer(Long playerId, AddItemDto itemDto) {
        return makePostRequest(
                String.format("/%d/items", playerId),
                itemDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public ItemRemovedResponse removeItemFromPlayer(Long playerId, Long itemId) {
        return makeDeleteRequest(
                String.format("/%d/items/%d", playerId, itemId),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public ItemUsedResponse useItem(Long playerId, Long itemId) {
        return makePostRequest(
                String.format("/%d/items/%d/use", playerId, itemId),
                "",
                new ParameterizedTypeReference<>() {
                }
        );
    }
}