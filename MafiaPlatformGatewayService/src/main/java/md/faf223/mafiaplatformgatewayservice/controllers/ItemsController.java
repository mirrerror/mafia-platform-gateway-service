package md.faf223.mafiaplatformgatewayservice.controllers; // Note: You'll likely move this to the inner service's package

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.AddItemDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDataDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemRemovedResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemUsedResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemsResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.ItemServiceCommunication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/character")
public class ItemsController {

    private final ItemServiceCommunication communication;

    @GetMapping("/{playerId}/items")
    public ApiResponse<ItemsResponse> getItems(@PathVariable Long playerId) {
        List<ItemDto> items = communication.getItemsForPlayer(playerId);
        return new ApiResponse<>(new ItemsResponse(items));
    }

    @PostMapping("/{playerId}/items")
    public ApiResponse<ItemDataDto> addItem(@PathVariable Long playerId, @RequestBody AddItemDto itemDto) {
        ItemDataDto item = communication.addItemToPlayer(playerId, itemDto);
        return new ApiResponse<>(item);
    }

    @DeleteMapping("/{playerId}/items/{itemId}")
    public ApiResponse<ItemRemovedResponse> removeItem(@PathVariable Long playerId, @PathVariable Long itemId) {
        ItemRemovedResponse response = communication.removeItemFromPlayer(playerId, itemId);
        return new ApiResponse<>(response);
    }

    @PostMapping("/{playerId}/items/{itemId}/use")
    public ApiResponse<ItemUsedResponse> useItem(@PathVariable Long playerId, @PathVariable Long itemId) {
        ItemUsedResponse response = communication.useItem(playerId, itemId);
        return new ApiResponse<>(response);
    }
}
