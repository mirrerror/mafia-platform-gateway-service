package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RumorPurchaseRequest {
    private UUID gameId;
    private int playerId;
    private UUID rumorId;
    private int cost;
}