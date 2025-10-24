package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsResponse {
    private List<ItemDto> items;
}
