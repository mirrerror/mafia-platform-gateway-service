package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {
    private UUID itemId;
    private int quantity = 1;
    private int playerId;
    private UUID gameId;
}