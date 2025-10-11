package md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseRumourDto {
    private Long gameId;
    private String rumourType;
    private long senderId;
    private long targetId;
}