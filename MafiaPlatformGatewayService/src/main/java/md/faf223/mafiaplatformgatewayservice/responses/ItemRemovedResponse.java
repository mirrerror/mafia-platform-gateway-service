package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRemovedResponse {
    private Long itemId;
    private boolean removed;
}
