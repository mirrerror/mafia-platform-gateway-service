package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDataDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemsResponse {
    private List<ItemDto> items;
}
