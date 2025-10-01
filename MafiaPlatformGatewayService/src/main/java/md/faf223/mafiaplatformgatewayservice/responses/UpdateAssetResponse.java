package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAssetResponse {
    private String slot;
    private Long previousAssetId;
    private Long newAssetId;

    public UpdateAssetResponse() {
        this.slot = null;
        this.previousAssetId = null;
        this.newAssetId = null;
    }
}
