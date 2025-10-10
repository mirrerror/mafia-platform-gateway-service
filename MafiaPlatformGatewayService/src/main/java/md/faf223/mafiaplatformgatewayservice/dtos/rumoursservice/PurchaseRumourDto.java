package md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice;

import lombok.Data;

@Data
public class PurchaseRumourDto {
    private long gameId;
    private String rumourType;
    private long senderId;
    private long targetId;
}