package md.faf223.mafiaplatformgatewayservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDataDto {
    private Long itemId;
    private Integer totalQuantity;
}
